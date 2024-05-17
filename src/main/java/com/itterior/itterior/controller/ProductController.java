package com.itterior.itterior.controller;

import com.itterior.itterior.dto.ProductPageRequestDTO;
import com.itterior.itterior.domain.page.PageResponseDTO;
import com.itterior.itterior.dto.ProductDTO;
import com.itterior.itterior.dto.ProductPageRequestDTOWithUsername;
import com.itterior.itterior.entity.Product;
import com.itterior.itterior.service.ProductService;
import com.itterior.itterior.util.CustomFileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Log4j2
@Tag(name = "Product", description = "Product API")
public class ProductController {
    private final ProductService productService;

    private final CustomFileUtil fileUtil;

    @GetMapping("/productions/{productId}")
    public ResponseEntity getOne(@PathVariable Long productId) {
        ProductDTO productDTO = this.productService.get(productId);

        log.info(productDTO);
        return ResponseEntity.ok().body(productDTO);
    }

    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> getList(ProductPageRequestDTO productPageRequestDTO) {
        log.info("controller GET LIST" + productPageRequestDTO);

        PageResponseDTO<ProductDTO> list = productService.getList(productPageRequestDTO);

        log.info(list);

        return list;
    }

    @GetMapping("/list/{username}")
    public PageResponseDTO<ProductDTO> getListWithUsername(ProductPageRequestDTOWithUsername dto) {
        log.info("controller GET LIST with Username" + dto);

        PageResponseDTO<ProductDTO> listWithUsername = this.productService.getListWithUsername(dto);

        log.info(listWithUsername);

        return listWithUsername;
    }

    @PostMapping("/register")
    public ResponseEntity register(ProductDTO productDTO) throws IOException {
        log.info("rgister: " + productDTO);
        List<MultipartFile> files = productDTO.getFiles();
        List<String> uploadFileNames = fileUtil.saveFiles(files);
        productDTO.setUploadFileNames(uploadFileNames);
        log.info(uploadFileNames);
        //서비스 호출
        Product registeredProduct = productService.register(productDTO);
        log.info(registeredProduct);
        return ResponseEntity.ok().body("register success");
    }

    @PutMapping("/modify/{pno}")
    public ResponseEntity modify(@PathVariable(name = "pno") Long pno, ProductDTO productDTO) throws IOException {

        productDTO.setPno(pno);

        ProductDTO oldProductDTO = productService.get(pno);

        //기존의 파일들 (데이터베이스에 존재하는 파일들 - 수정 과정에서 삭제되었을 수 있음)
        List<String> oldFileNames = oldProductDTO.getUploadFileNames();

        //새로 업로드 해야 하는 파일들
        List<MultipartFile> files = productDTO.getFiles();

        //새로 업로드되어서 만들어진 파일 이름들
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        //화면에서 변화 없이 계속 유지된 파일들
        List<String> uploadedFileNames = productDTO.getUploadFileNames();

        //유지되는 파일들  + 새로 업로드된 파일 이름들이 저장해야 하는 파일 목록이 됨
        if (currentUploadFileNames != null && currentUploadFileNames.size() > 0) {

            uploadedFileNames.addAll(currentUploadFileNames);

        }
        //수정 작업
        productService.modify(productDTO);

        if (oldFileNames != null && oldFileNames.size() > 0) {

            //지워야 하는 파일 목록 찾기
            //예전 파일들 중에서 지워져야 하는 파일이름들
            List<String> removeFiles = oldFileNames
                    .stream()
                    .filter(fileName -> uploadedFileNames.indexOf(fileName) == -1).collect(Collectors.toList());

            //실제 파일 삭제
            fileUtil.deleteFiles(removeFiles);
        }
        return ResponseEntity.ok().body("modify success");
    }

    @Transactional
    @DeleteMapping("/remove/{pno}")
    public ResponseEntity remove(@PathVariable("pno") Long pno) {
        //삭제해야할 파일들 알아내기
        List<String> oldFileNames = productService.get(pno).getUploadFileNames();
        productService.remove(pno);
        fileUtil.deleteFiles(oldFileNames);
        return ResponseEntity.ok().body("delete success");
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) throws IOException {
        log.info(fileName);
        return fileUtil.getFile(fileName);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ProductDTO>> getPopularProducts() {
        // viewCount가 높은 순서대로 10개의 Product를 가져옴
        List<ProductDTO> popularProducts = productService.findTop10ByViewCount();
        return ResponseEntity.ok().body(popularProducts);
    }


    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        try {
            List<ProductDTO> products = productService.searchProducts(query);
            log.info(products);
            long endTime = System.currentTimeMillis();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/searchInfinite")
    public ResponseEntity<?> searchProductsInfiniteScroll(@RequestParam String query, Long lastPno) {
        try {
            PageResponseDTO<ProductDTO> products = productService.searchProductsInfiniteScroll(query, lastPno);
            log.info(products);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
