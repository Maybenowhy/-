package com.an.service.impl;

import com.an.entity.Product;
import com.an.exception.BusinessException;
import com.an.mapper.ProductMapper;
import com.an.result.PageResult;
import com.an.service.ProductService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 20:42
 * @description:
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 条件查询商品列表
     */
    @Override
    public List<Product> list(Product product) {
        return productMapper.list(product);
    }

    /**
     * 根据主键查询商品
     */
    @Override
    public Product getById(Long id) {
        try {
            Product product = productMapper.getById(id);
            if (product != null && product.getIsActive() == 0) {
                throw new BusinessException("商品已下架");
            }
            return product;
        } catch (Exception e) {
            log.error("查询商品详情异常: {}", e.getMessage());
            throw new BusinessException("获取商品详情失败");
        }
    }

    @Override
    public PageResult pageQuery(Integer page, Integer pageSize, Product product) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 调用 Mapper 的分页查询
        List<Product> records = productMapper.pageQuery(offset, pageSize, product);

        // 获取总数
        Long total = productMapper.countByCondition(product);

        return new PageResult(total, records);
    }

    @Override
    public void add(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        product.setCategoryId(1L);
        product.setIsActive(1);
        try{
            productMapper.add(product);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void update(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        try{
            product.setCategoryId(1L);
            productMapper.update(product);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void softDelete(Long id) {
        productMapper.softDelete(id);
    }

    @Override
    public void batchUpdateActive(List<Long> ids, Integer isActive) {
        productMapper.batchUpdateActive(ids, isActive);
    }

    @Override
    public void updateStock(Long id, Integer stockQuantity) {
        productMapper.updateStock(id, stockQuantity);
    }

}
