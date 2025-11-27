package com.an.mapper;

import com.an.entity.Order;
import com.an.vo.DailySalesVO;
import com.an.vo.SalesSummaryVO;
import com.an.vo.TopProductVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    // -------------------- 顾客端原有 --------------------

    /** 插入订单 */
    void insert(Order order);

    /** 根据ID查询订单 */
    Order getById(@Param("id") Long id);

    /** 查询用户订单列表 */
    List<Order> listByUserId(@Param("userId") Long userId);

    /** 更新订单状态（如支付成功） */
    void updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /** 更新支付时间 */
    void updatePayTime(@Param("id") Long id, @Param("payTime") String payTime);

    /** 全字段更新 */
    void update(Order order);



    // -------------------- 销售端新增部分 --------------------

    /**
     * 销售端分页查询（按条件）
     */
    List<Order> adminList(Order condition);

    /**
     * 销售端删除（逻辑删除）
     */
    void deleteById(@Param("id") Long id);

    Order getFullById(@Param("id") Long id);




    // -------------------- 销售统计报表 --------------------

    /** 日销售额统计 */
    List<DailySalesVO> dailySales(@Param("begin") String begin,
                                  @Param("end") String end);

    /** 销售概览（总销售额/订单数/客单价） */
    SalesSummaryVO summary(@Param("begin") String begin,
                           @Param("end") String end);
}
