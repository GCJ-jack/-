package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j

public class DishController {

    @Autowired
    DishService dishService;


    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */

    @PostMapping()
    @ApiOperation("新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询：{}",dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     *  * 菜品批量删除
     *  * @param ids
     *  * @return
     *  */

    @DeleteMapping()
    @ApiOperation("批量删除")

    public Result<String> delete(@RequestParam List<Long> ids){
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("返回具体的菜品信息")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("获取菜品id" + id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 根据id修改菜品信息
     * @param dishDTO
     * @return
     */


    @PutMapping()
    @ApiOperation("更改菜品信息以及菜品的口味")
    public Result<String> updateWithFlavor(@RequestBody DishDTO dishDTO){
        log.info("修改菜品信息中 " + dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改菜品的状态")
    public Result<String> startOrStop(@PathVariable Integer status, Long id){
        log.info("修改菜品信息中 " + status);
        dishService.startOrStop(status,id);
        return Result.success();
    }


}
