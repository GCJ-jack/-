package com.sky.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdersPaymentDTO {

    //订单号
    private String orderNumber;

    //付款方式
    private Integer payMethod;


}
