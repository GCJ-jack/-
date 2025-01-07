package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealIdsByDishIds(List<Long> ids);

    /**
     * 保存套餐和菜品的关联关系
     * @param dishes
     */
    void insertBatch(List<SetmealDish> dishes);

}