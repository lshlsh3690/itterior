package com.itterior.itterior.repository;

import com.itterior.itterior.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> findAllAndCountQueries();
}
