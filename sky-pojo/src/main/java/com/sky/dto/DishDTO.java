package com.sky.dto;

import com.sky.entity.DishFlavor;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO implements Serializable {

    private Long id;
    private Long categoryId;

    private String description;

    private Integer status;
    private BigDecimal price;
    private String image;
    private String Name;

    //口味
    private List<DishFlavor> flavors = new ArrayList<>();
}
