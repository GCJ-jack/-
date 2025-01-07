package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SetmealMapper {
    Integer countByMap(Map map);

    Setmeal getById(Long setmealId);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);
}