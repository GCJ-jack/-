package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    AddressBookMapper addressBookMapper;

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;




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

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        Orders ordersDB = orderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    @Override
    public PageResult pageQueryForUser(int pageNum, int pageSize, Integer status) {

        // 设置分页
        PageHelper.startPage(pageNum,pageSize);
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();

        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);

        List<OrderVO> list = new ArrayList<>();


        // 查询出订单明细，并封装入OrderVO进行响应
        if(page!=null && page.getTotal() > 0){
            for (Orders orders : page){
                Long orderId = orders.getId();

                // 查询订单明细
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();

                BeanUtils.copyProperties(orders, orderVO);

                orderVO.setOrderDetailList(orderDetailList);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(),list);
    }

    @Override
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = orderMapper.getById(id).getUserId();
        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        List<ShoppingCart> shoppingCartList = new ArrayList<>();
//        //创建购物车对象
//        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
//            ShoppingCart shoppingCart = new ShoppingCart();
//
//            // 将原订单详情里面的菜品信息重新复制到购物车对象中
//            BeanUtils.copyProperties(x, shoppingCart, "id");
//            shoppingCart.setUserId(userId);
//            shoppingCart.setCreateTime(LocalDateTime.now());
//
//            return shoppingCart;
//        }).collect(Collectors.toList());

        for(OrderDetail orderDetail:orderDetailList){
            ShoppingCart shoppingCart = new ShoppingCart();

            //将原订单的菜品信息复制粘贴到购物车对象中
            BeanUtils.copyProperties(orderDetail,shoppingCart,"id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCartList.add(shoppingCart);
        }

        shoppingCartMapper.insertBatch(shoppingCartList);
    }

    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }


    /**
     * 拒单
     *
     * @param ordersRejectionDTO
     */
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) throws Exception {

        //获得订单id
        Long orderId = ordersRejectionDTO.getId();
        //根据id查询订单
        Orders orderDB = orderMapper.getById(orderId);
        //商家拒单其实就是将订单状态修改为“已取消”
        //只有订单处于“待接单”状态时可以执行拒单操作
        if(orderDB == null && orderDB.getStatus() != Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //支付状态
        Integer payStatus = orderDB.getPayStatus();

        if (payStatus == Orders.PAID) {
            //用户已支付，需要退款
            String refund = weChatPayUtil.refund(
                    orderDB.getNumber(),
                    orderDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("申请退款：{}", refund);
        }

        Orders orders = new Orders();

        //商家拒单时需要指定拒单原因
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelTime(LocalDateTime.now());
        //商家拒单时，如果用户已经完成了支付，需要为用户退款
        orderMapper.update(orders);
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
    @Override
    public OrderVO details(Long id) {
        OrderVO orderVO = new OrderVO();
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);
        Orders orders = orderMapper.getById(id);
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);
        return orderVO;
    }


//    商家已接单状态下，用户取消订单需电话沟通商家
//    派送中状态下，用户取消订单需电话沟通商家
//    如果在待接单状态下取消订单，需要给用户退款
//    取消订单后需要将订单状态修改为已取消
    @Override
    public void userCancelById(Long id) throws Exception {
        Orders orders = orderMapper.getById(id);
        Long orderId = orders.getId();

        //检查穿过来的订单是否为空
        //如果是空抛出异常
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消

        if(orders.getStatus()>2){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //    派送中状态下，用户取消订单需电话沟通商家
        //    如果在待接单状态下取消订单，需要给用户退款
        //    取消订单后需要将订单状态修改为已取消

        Orders orderNew = new Orders();
        orderNew.setId(orderId);

        // 订单处于待接单状态下取消，需要进行退款
        if (orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    orders.getNumber(), //商户订单号
                    orders.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            orderNew.setPayStatus(Orders.REFUND);
        }

        orderNew.setStatus(Orders.CANCELLED);
        orderNew.setCancelReason("用户取消");
        orderNew.setCancelTime(LocalDateTime.now());
        orderMapper.update(orderNew);
    }
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
