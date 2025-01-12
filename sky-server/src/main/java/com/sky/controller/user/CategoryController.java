package com.sky.controller.user;


import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端-分类接口")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation(value = "根据类型查询分类")
    public Result<List<Category>> list(Integer type){
        List<Category> categories= categoryService.list(type);
        return Result.success(categories);
    }
}
