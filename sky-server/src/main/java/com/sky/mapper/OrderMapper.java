package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.entity.Orders;
import com.sky.dto.OrdersPageQueryDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 插入订单
     * @param orders
     */
    void insert(Orders orders);

    /**
     * 根据订单号和用户id查询订单
     * @param orderNumber
     * @param userId
     */
    Orders getByNumberAndUserId(String orderNumber, Long userId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 分页查询
     * @param
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);
}
