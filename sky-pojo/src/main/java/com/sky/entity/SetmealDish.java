package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SetmealDish implements Serializable {

    private long id;

    private long setmealId;

    private long dishId;

    private String name;

    private BigDecimal price;

    private Integer copies;
}
