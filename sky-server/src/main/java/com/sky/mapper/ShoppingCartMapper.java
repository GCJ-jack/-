package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    @AutoFill(OperationType.INSERT)
    void insert(ShoppingCart shoppingCart);

    /**
     * * 条件查询
     * * @param shoppingCart
     * * @return     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    void updateNumberById(ShoppingCart shoppingCart);

    void deleteByUserId(Long userId);
}
