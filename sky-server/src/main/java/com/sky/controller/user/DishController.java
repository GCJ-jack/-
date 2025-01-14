package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController("userDishController")
@Slf4j
@RequestMapping("/user/dish")
@Api(tags = "C端-菜品浏览接口")
public class DishController {

    @Autowired
    DishService dishService;


    @Autowired
    RedisTemplate redisTemplate;


    @ApiOperation("返回菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId){
        // 创造redis键
        String key = "dish_" + categoryId;
        // 通过键来读取值
        List<DishVO> redis_dish = (List<DishVO>) redisTemplate.opsForValue().get(key);
        //存在缓存数据 直接返还给前端
        if (redis_dish!=null){
            return Result.success(redis_dish);
        }

        log.info("类型id "+ categoryId);
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);

        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);

        // 该菜品不存在于redis中
        // 开始存取 以后方便阅读
        redisTemplate.opsForValue().set(key,list);

        return Result.success(list);
    }


}
