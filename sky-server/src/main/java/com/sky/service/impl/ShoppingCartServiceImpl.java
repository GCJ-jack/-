package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    ShoppingCartMapper shoppingCartMapper;

    @Autowired
    DishMapper dishMapper;

    @Autowired
    SetmealMapper setmealMapper;


    @Override
    public void insert(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        //只能查询自己的购物车数据
        log.info("用户的id： " + BaseContext.getCurrentId());
        log.info("购物车中菜品的id " + shoppingCartDTO.getDishId());
        log.info("购物车中套餐的id " + shoppingCartDTO.getSetmealId());
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        //判断当前购物车里面是否存在此商品
        log.info("已有该货物的数量 "+ shoppingCartList.size());
        if(shoppingCartList!=null&&shoppingCartList.size()==1){
            //如果已经存在数量增加1
            shoppingCart = shoppingCartList.get(0);

            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            log.info("添加后的货品数量 " + shoppingCart.getNumber());
            shoppingCartMapper.updateNumberById(shoppingCart);
        }else {
            //如果不存在数量加一
            Long dishId = shoppingCart.getDishId();
            log.info("菜品id " + dishId);
            if(dishId!=null){
                //添加到购物车的是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            }else {
                //添加到购物车的是套餐
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                log.info("套餐 " + shoppingCart);
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> showShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(BaseContext.getCurrentId()).build();
        return shoppingCartMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCart(Long userId) {
        shoppingCartMapper.deleteByUserId(userId);
    }

    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //设置查询条件 是否该物品存在于购物车中
        ShoppingCart shoppingCart = new ShoppingCart();

        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);

        shoppingCart.setUserId(BaseContext.getCurrentId());
        //调取到该用户的购物车
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);

        if (shoppingCartList!=null&&shoppingCartList.size()>0){

            shoppingCart = shoppingCartList.get(0);


            Integer number = shoppingCart.getNumber();

            if(number == 1){
                //如果该货品只剩下一个 就删除
                shoppingCartMapper.deleteById(shoppingCart.getId());
            }else {
                //如果该货物购物车中存在多个 就数量上减少一个
                shoppingCart.setNumber(number-1);
                shoppingCartMapper.updateNumberById(shoppingCart);
            }
        }

    }

}
