package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.SetmealDish;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired

    SetmealMapper setmealMapper;

    @Autowired

    SetmealDishMapper setmealDishMapper;


    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        return null;
    }

    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        return null;
    }

    @Override

    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        //复制特征到entity
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //插入数据库
        setmealMapper.insert(setmeal);
        //取出套餐id
        Long setmealId = setmeal.getId();
        //提取出套餐中的菜品
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();

        //如果菜品的数量大于0那么批量插入菜品加上套餐id到数据库中
        dishes.forEach(dish -> dish.setDishId(setmealId));
        //批量插入被包括在套餐中的菜品
        setmealDishMapper.insertBatch(dishes);
    }
}
