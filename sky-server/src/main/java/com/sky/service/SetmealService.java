package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetmealService {
    List<DishItemVO> getDishItemById(Long id);

    List<Setmeal> list(Setmeal setmeal);



    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */

    void saveWithDish(SetmealDTO setmealDTO);
}
