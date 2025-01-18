package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersSubmitDTO implements Serializable {

    private Long addressBookId;

    private BigDecimal amount;

    private Integer deliveryStatus;

    private String estimatedDeliveryTime;

    private Integer packAmount;

    private Integer payMethod;

    private String remark;

    private Integer tablewareNumber;

    private Integer tablewareStatus;
}
