package com.sky.controller.user;


import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ShoppingCartController")
@RequestMapping("/user/shoppingCart")
@Slf4j
@Api("c端-用户浏览购物车接口")
public class ShoppingCartController {

    @Autowired
    ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("添加选购商品到购物车")
    public Result<String> save(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加货品到购物车 " + shoppingCartDTO);
        shoppingCartService.insert(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("展示加入购物车中的商品")
    public Result<List<ShoppingCart>> list(){
        return Result.success(shoppingCartService.showShoppingCart());
    }
}
