package com.itterior.itterior.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CustomS3UtilTest {

    @Test
    void uploadFiles() {
//        log.info("update test........");
//
//        Path filePath = new java.io.File("E:\\backend\\it-terior\\upload\\default.jpeg").toPath();
//
//        List<Path> fileList = List.of(filePath);
//
//        s3Util.uploadFiles(fileList, false);
    }

    @Test
    void getFiles(){
//        log.info("get Files.......");
//
//        String fileName = "default.jpeg";
//
//        s3Util.getFileUrl(fileName);
    }

    @Test
    void deleteFiles() {
    }
}