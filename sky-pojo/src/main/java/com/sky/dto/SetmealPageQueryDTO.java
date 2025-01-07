package com.sky.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SetmealPageQueryDTO implements Serializable {

    private int page;

    private int pageSize;

    private String name;
    // 已启售数量
    private Integer sold;

    // 已停售数量
    private Integer discontinued;

    //分类id
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    private Integer status;



}
