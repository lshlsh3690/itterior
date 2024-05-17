package com.itterior.itterior.repository;

import com.itterior.itterior.entity.Product;
import com.itterior.itterior.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;


@SpringBootTest
@Slf4j
class ProductRepositoryTest {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;
//    @Test
//    public void testInsert() {
//
//        User user = userRepository.selectOneByUserName("lsls").get();
//
//
//        for (int i = 1; i < 1000; i++) {
//
//            Product product = Product.builder()
//                    .pname("상품" + i)
//                    .price(100 * i)
//                    .pdesc("상품설명 " + i)
//                    .categoryNum(Long.valueOf((i + 1) % 3) + 1)
//                    .build();
//
//            product.setSeller(user);
//
//            //2개의 이미지 파일 추가
//            product.addImageString("IMAGE1.jpg");
//            product.addImageString("IMAGE2.jpg");
//
//            productRepository.save(product);
//
//            log.info("-------------------");
//        }
//    }

//    @Test
//    public void increaseViewCount() {
//        Product product = productRepository.findById(1L).get();
//
//        product.setViewCount(10L);
//        productRepository.save(product);
//        product = productRepository.findById(2L).get();
//        product.setViewCount(9L);
//        productRepository.save(product);
//
//        product = productRepository.findById(3L).get();
//        product.setViewCount(8L);
//        productRepository.save(product);
//
//        product = productRepository.findById(4L).get();
//        product.setViewCount(7L);
//        productRepository.save(product);
//
//        product = productRepository.findById(5L).get();
//        product.setViewCount(6L);
//        productRepository.save(product);
//
//        product = productRepository.findById(6L).get();
//        product.setViewCount(5L);
//        productRepository.save(product);
//
//        product = productRepository.findById(7L).get();
//        product.setViewCount(4L);
//        productRepository.save(product);
//
//        product = productRepository.findById(8L).get();
//        product.setViewCount(3L);
//        productRepository.save(product);
//
//        product = productRepository.findById(9L).get();
//        product.setViewCount(2L);
//        productRepository.save(product);
//
//        product = productRepository.findById(10L).get();
//        product.setViewCount(1L);
//        productRepository.save(product);
//    }



//    @Test
//    @Transactional
//    public void testFindByPnameContainingIgnoreCase(){
//        String searchQuery = "상품";
//        List<Product> result = productRepository.searchWithQuery(searchQuery);
//        log.info(result.toString());
//    }
}