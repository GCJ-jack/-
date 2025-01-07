package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.*;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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


    /**
     * 返回查询的页数结果
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询员工信息")
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        //select * from employee limit 10,20
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Employee> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        // for循环遍历每一个id删除相关的套餐
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            // 检查是否该套餐是 启售中 如果套餐是启售中的状态 抛出异常
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                //起售中的套餐不能删除
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
                }
            }
        );

        ids.forEach(id ->{
            // 删除套餐
            setmealMapper.deleteById(id);
            // 删除相关的套餐之后 删除 菜品和该套餐的绑定
            setmealDishMapper.deleteBySetmealId(id);
        });

    }

    @Override
    public SetmealVO getByIdWithDish(Long id) {
        //根据id获得套餐信息
        Setmeal setmeal = setmealMapper.getById(id);

        //根据dishid获得口味信息
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        //将查询到的数据封装到vo
        SetmealVO setmealVO = new SetmealVO();

        BeanUtils.copyProperties(setmeal,setmealVO);

        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        // 更新套餐的信息
        Setmeal setmeal = new Setmeal();

        BeanUtils.copyProperties(setmealDTO,setmeal);

        setmealMapper.update(setmeal);

        Long setmealId = setmealDTO.getId();

        // 批量删除 套餐中的菜品然后重新加入
        setmealDishMapper.deleteBySetmealId(setmealId);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        setmealDishMapper.insertBatch(setmealDishes);
    }


}
