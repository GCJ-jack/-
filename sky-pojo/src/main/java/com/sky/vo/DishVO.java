package com.sky.vo;


import com.sky.entity.DishFlavor;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "餐品返回的数据格式")

public class DishVO implements Serializable {

    @ApiModelProperty("主键值")
    private long id;

    @ApiModelProperty("菜品的名称")
    private String name;

    @ApiModelProperty("菜品分类id")
    private Long categoryId;
    @ApiModelProperty("菜品价格")
    private BigDecimal price;
    @ApiModelProperty("图片")
    private String image;
    @ApiModelProperty("描述信息")
    private String description;
    //0 停售 1 起售
    private Integer status;

    @ApiModelProperty("口味")
    private List<DishFlavor> flavors = new ArrayList<>();

}
