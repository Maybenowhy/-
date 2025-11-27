package com.an.mapper;

import com.an.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    @Select("SELECT * FROM shopping_carts WHERE user_id = #{userId}")
    List<ShoppingCart> list(Long userId);

    @Select("SELECT * FROM shopping_carts WHERE user_id = #{userId} AND product_id = #{productId}")
    ShoppingCart getByUserIdAndProductId(Long userId, Long productId);

    @Insert("INSERT INTO shopping_carts (user_id, product_id, quantity, created_at, updated_at) VALUES " +
            "(#{userId}, #{productId}, #{quantity}, #{createdAt}, #{updatedAt})")
    void insert(ShoppingCart cart);

    @Update("UPDATE shopping_carts SET quantity = #{quantity}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateQuantity(ShoppingCart cart);

    @Delete("DELETE FROM shopping_carts WHERE user_id = #{userId} AND product_id = #{productId}")
    void delete(Long userId, Long productId);

    /**
     * 根据用户ID清空购物车
     */
    @Delete("DELETE FROM shopping_carts WHERE user_id = #{userId}")
    void clear(@Param("userId") Long userId);
}
