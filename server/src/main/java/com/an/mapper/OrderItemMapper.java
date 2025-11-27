package com.an.mapper;

import com.an.entity.OrderItem;
import com.an.vo.TopProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    /** 插入订单项 */
    void insert(OrderItem orderItem);

    /** 根据订单ID查询订单明细 */
    List<OrderItem> listByOrderId(@Param("orderId") Long orderId);

    /** ------- 销售端新增：销量排行 ------- */
    List<TopProductVO> topSales(@Param("limit") Integer limit);
}
