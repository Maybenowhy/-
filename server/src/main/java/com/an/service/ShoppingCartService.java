package com.an.service;

import com.an.entity.ShoppingCart;

import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 21:20
 * @description:
 */
public interface ShoppingCartService {

    void addToCart(ShoppingCart cart);

    List<ShoppingCart> list(Long userId);

    void delete(Long userId, Long productId);

}
