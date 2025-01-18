package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    AddressBookMapper addressBookMapper;

    @Autowired
    ShoppingCartMapper shoppingCartMapper;


    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        // 异常情况检查购物地址是否为空
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook==null){
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //通过用户id获得当前用户的购物车信息
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);

        //查询当前用户的购物车数据
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        //如果当先购物车为空 抛出异常
        if(shoppingCartList==null&&shoppingCartList.size()==0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }


        //开始构造订单数据
        Orders orders = new Orders();

        BeanUtils.copyProperties(ordersSubmitDTO,orders);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setUserId(userId);
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setOrderTime(LocalDateTime.now());


        orderMapper.insert(orders);

        //明细订单中的信息
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart cart : shoppingCartList){
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }

        // 想数据库中批量插入订单细节
        orderDetailMapper.insertBatch(orderDetailList);
        //删除购物车的货品
        shoppingCartMapper.deleteByUserId(userId);
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }
//
//    @Override
//    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        return null;
//    }
//
//    @Override
//    public void paySuccess(String outTradeNo) {
//
//    }
//
//    @Override
//    public PageResult pageQueryForUser(int pageNum, int pageSize, Integer status) {
//        return null;
//    }
//
//    @Override
//    public OrderVO details(Long id) {
//        return null;
//    }
//
//    @Override
//    public void userCancelById(Long id) throws Exception {
//
//    }
//
//    @Override
//    public void repetition(Long id) {
//
//    }
//
//    @Override
//    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
//        return null;
//    }
//
//    @Override
//    public OrderStatisticsVO statistics() {
//        return null;
//    }
//
//    @Override
//    public void confirm(OrdersCancelDTO ordersCancelDTO) {
//
//    }
//
//    @Override
//    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {
//
//    }
//
//    @Override
//    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
//
//    }
//
//    @Override
//    public void delivery(Long id) {
//
//    }
//
//    @Override
//    public void complete(Long id) {
//
//    }
//
//    @Override
//    public void reminder(Long id) {
//
//    }
}
