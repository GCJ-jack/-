package com.sky.controller.user;


import com.sky.dto.OrdersSubmitDTO;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userOrderCOntroller")
@RequestMapping("/user/order")
@Slf4j()
@Api(tags = "C端订单接口")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public OrderSubmitVO submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("提交订单到服务端 " + ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return orderSubmitVO;
    }
}
