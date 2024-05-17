package com.itterior.itterior.util;

import com.itterior.itterior.exception.CustomUnsupportedFormatException;
import io.awspring.cloud.s3.ObjectMetadata;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.tasks.UnsupportedFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;
    private final S3Client s3Client;


    @Value("upload")
    private String uploadPath;
    @PostConstruct
    public void init() {
        File tempFolder = new File(uploadPath);
        if (tempFolder.exists() == false) {
            tempFolder.mkdir();
        }

    }

    public List<String> saveFiles(List<MultipartFile> files)
            throws RuntimeException, IOException {
        if (files == null || files.size() == 0) {
            return List.of();
        }
        List<String> uploadNames = new ArrayList<>();
        long lno = 0;
        for (MultipartFile multipartFile : files) {
            String originalFileName = multipartFile.getOriginalFilename();
            log.info(lno++ + "번-----------------------------------------------------------------");
            // 한글 파일명을 안전하게 인코딩
            String savedName = transferSavedName(originalFileName);
            String contentType = multipartFile.getContentType();
            //이미지 여부 확인
            if (contentType != null && contentType.startsWith("image")) {
                // GIF 파일인 경우 별도 처리
                // 썸네일 생성
                byte[] thumbnailBytes = createThumbnail(multipartFile, 500, 200);
                // 바이트 배열을 ByteArrayInputStream으로 변환
                ByteArrayInputStream inputStream = new ByteArrayInputStream(thumbnailBytes);

                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key("s_" + savedName)
                        .contentType(contentType)
                        .contentLength((long) thumbnailBytes.length)
                        .build();

                //s3 upload
                s3Client.putObject(request, RequestBody.fromInputStream(inputStream, thumbnailBytes.length));
                uploadNames.add("s_" + savedName);


                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(multipartFile.getBytes());
                PutObjectRequest request1 = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(savedName)
                        .contentLength(multipartFile.getSize())
                        .contentType(contentType)
                        .build();

                s3Client.putObject(request1, RequestBody.fromInputStream(byteArrayInputStream, multipartFile.getSize()));
                uploadNames.add(savedName);
            }
        }
        log.info(uploadNames.toString());
        return uploadNames;
    }

    public String saveProFile(MultipartFile proFile)
            throws RuntimeException, IOException {
        if (proFile == null ) {
            return null;
        }
        String originalFileName = proFile.getOriginalFilename();
        /*이름 변환*/
        String savedName = transferSavedName(originalFileName);

        //s3upload
        CompletableFuture<PutObjectResponse> PutObjectResponse1 = CompletableFuture.supplyAsync(() -> {
            try {
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(proFile.getBytes());
                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(savedName)
                        .contentLength(proFile.getSize())
                        .contentType(proFile.getContentType())
                        .build();

                return s3Client.putObject(request, RequestBody.fromInputStream(byteArrayInputStream, proFile.getSize()));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });

        long startTime = System.currentTimeMillis();
        String contentType = proFile.getContentType();
        byte[] thumbnailBytes = createThumbnail(proFile, 200, 200);

        long endTime = System.currentTimeMillis();

        log.info("썸네일 만들기 {}", endTime-startTime);
        CompletableFuture<PutObjectResponse> PutObjectResponse2 = CompletableFuture.supplyAsync(() -> {
                if (contentType != null && contentType.startsWith("image")) {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(thumbnailBytes);
                    PutObjectRequest request = PutObjectRequest.builder()
                            .bucket(bucket)
                            .key("us_" + savedName)
                            .contentLength((long) thumbnailBytes.length)
                            .contentType(contentType)
                            .build();

                    return s3Client.putObject(request, RequestBody.fromInputStream(inputStream, thumbnailBytes.length));
                }else {
                    return null;
                }
        });

        log.info(PutObjectResponse1.toString());
        log.info(PutObjectResponse2.toString());
        return savedName;
    }

    private static String transferSavedName(String originalFileName) {
        String encodedFileName= null;
        try {
            encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            encodedFileName = originalFileName;
        }
        String savedName =
                UUID.randomUUID() +
                        "_" +
                        encodedFileName;
        return savedName;
    }


    public ResponseEntity<Resource> getFile(String fileName) throws IOException {
        String encodedFileName = null;
        try {
            // 한글 파일명을 안전하게 인코딩
            encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");

        } catch (Exception e) {
            encodedFileName = fileName;
        }

        log.info(encodedFileName);
        Resource imageFromS3 = getImageFromS3( encodedFileName);
        if (imageFromS3 == null || !imageFromS3.isReadable()) {
            imageFromS3 = getImageFromS3("default.jpeg");
        }

        HttpHeaders headers = new HttpHeaders();
        String contentType ="application/octet-stream";
        try {
            headers.add("Content-Type", contentType);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(imageFromS3.contentLength())
                .body(imageFromS3);
    }

    public void deleteFiles(List<String> fileNames) {
        if (fileNames == null || fileNames.size() == 0) {
            return;
        }
        fileNames.forEach(fileName -> {
            String thumbnailFileName = "s_" + fileName;

            CompletableFuture.runAsync(() -> {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(fileName)
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
            });

            CompletableFuture.runAsync(() -> {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(thumbnailFileName)
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
            });
        });
    }

    private byte[] createThumbnail(MultipartFile file, int height, int width) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file.getInputStream())
                .size(height, width) // 썸네일 크기 설정
                .outputFormat("jpg") // 썸네일 이미지 형식 설정
                .toOutputStream(outputStream);
        return outputStream.toByteArray(); // 바이트 배열로 썸네일 반환
    }

    public Resource getImageFromS3(String imageName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(imageName)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes =
                    s3Client.getObjectAsBytes(getObjectRequest);
            byte[] data = objectBytes.asByteArray();
            // 바이트 배열을 리소스로 변환
            ByteArrayResource resource = new ByteArrayResource(data);
            return resource;
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }
}
