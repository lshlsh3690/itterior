package com.itterior.itterior.service;


import com.itterior.itterior.dto.ProductPageRequestDTO;
import com.itterior.itterior.domain.page.PageResponseDTO;
import com.itterior.itterior.dto.ProductDTO;
import com.itterior.itterior.dto.ProductPageRequestDTOWithUsername;
import com.itterior.itterior.entity.Product;

import java.util.List;

public interface ProductService {
    PageResponseDTO<ProductDTO> getList(ProductPageRequestDTO pageRequestDTO);

    PageResponseDTO<ProductDTO>getListWithUsername(ProductPageRequestDTOWithUsername productPageRequestDTOWithUsername);

    ProductDTO get(Long pno);


    Product register(ProductDTO productDTO);

    void modify(ProductDTO productDTO);

    void remove(Long pno);

    List<ProductDTO> findTop10ByViewCount();

    List<ProductDTO> searchProducts(String query);

    PageResponseDTO<ProductDTO> searchProductsInfiniteScroll(String query, Long lastPno);
}
