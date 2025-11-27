package com.an.mapper;

import com.an.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author: Mystery
 * @url: www.an.com
 * @date: 2025/11/14 20:42
 * @description:
 */
@Mapper
public interface ProductMapper {

    // ----------- 顾客端功能（你已有） ----------
    List<Product> list(Product product);
    Product getById(Long id);
    @Update("UPDATE products SET stock_quantity = stock_quantity - #{quantity} WHERE id = #{productId}")
    void decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);


    // ----------- 销售端新增 ----------
    void add(Product product);

    // ----------- 销售端修改 ----------
    void update(Product product);

    // ----------- 删除 / 逻辑删除 ----------
    void softDelete(Long id);

    // ----------- 批量上下架 ----------
    void batchUpdateActive(@Param("ids") List<Long> ids, @Param("isActive") Integer isActive);

    // ----------- 修改库存（后台设值） ----------
    void updateStock(@Param("productId") Long productId,
                     @Param("stockQuantity") Integer stockQuantity);

    // ----------- 模糊搜索 ----------
    List<Product> search(Product product);

    // ----------- 后台分页查询 ----------
    List<Product> adminList(Product product);

    List<Product> pageQuery(int offset, Integer pageSize, Product product);

    Long countByCondition(Product product);
}
