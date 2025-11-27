package com.an.service;

import com.an.entity.Product;
import com.an.result.PageResult;

import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 20:40
 * @description:
 */
public interface ProductService {

    List<Product> list(Product product);

    Product getById(Long id);

    PageResult pageQuery(Integer page, Integer pageSize, Product product);

    void add(Product product);

    void update(Product product);

    void softDelete(Long id);

    void batchUpdateActive(List<Long> ids, Integer isActive);

    void updateStock(Long id, Integer stockQuantity);
}

