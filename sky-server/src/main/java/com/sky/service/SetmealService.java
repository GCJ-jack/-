package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    List<DishItemVO> getDishItemById(Long id);

    List<Setmeal> list(Setmeal setmeal);



    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */

    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 返回套餐的分页查询
     * @param setmealPageQueryDTO
     * @return
     */

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id来删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    SetmealVO getByIdWithDish(Long id);
}
