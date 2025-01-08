package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Employee;
import com.sky.entity.Setmeal;
//import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface SetmealMapper {
    Integer countByMap(Map map);

    Setmeal getById(Long setmealId);

//    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);

    Page<Employee> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 删除菜品表中的菜品数据
     * @param id
     */
    void deleteById(Long id);

    void update(Setmeal setmeal);
}