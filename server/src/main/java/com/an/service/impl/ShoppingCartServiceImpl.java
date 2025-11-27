package com.an.service.impl;

import com.an.entity.Product;
import com.an.entity.ShoppingCart;
import com.an.exception.BusinessException;
import com.an.mapper.ProductMapper;
import com.an.mapper.ShoppingCartMapper;
import com.an.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 添加购物车
     */
    @Override
    public void addToCart(ShoppingCart cart) {

        Long userId = cart.getUserId();
        Long productId = cart.getProductId();
        Integer quantity = cart.getQuantity();

        // 1. 校验商品存在且上架
        Product product = productMapper.getById(productId);
        if (product == null || product.getIsActive() != 1) {
            throw new BusinessException("商品不存在或未上架");
        }

        // 2. 校验库存足够
        if (product.getStockQuantity() < quantity) {
            throw new BusinessException("库存不足");
        }

        // 3. 查询购物车是否已有该商品
        ShoppingCart exist = shoppingCartMapper.getByUserIdAndProductId(userId, productId);

        if (exist != null) {
            // 已存在 → 数量累加
            exist.setQuantity(exist.getQuantity() + quantity);
            exist.setUpdatedAt(LocalDateTime.now());
            shoppingCartMapper.updateQuantity(exist);
        } else {
            // 不存在 → 插入新记录
            cart.setCreatedAt(LocalDateTime.now());
            cart.setUpdatedAt(LocalDateTime.now());
            shoppingCartMapper.insert(cart);
        }
    }

    /**
     * 查询购物车
     */
    @Override
    public List<ShoppingCart> list(Long userId) {
        return shoppingCartMapper.list(userId);
    }

    /**
     * 删除商品
     */
    @Override
    public void delete(Long userId, Long productId) {
        shoppingCartMapper.delete(userId, productId);
    }
}
