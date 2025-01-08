package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Key;

@RestController()
@RequestMapping("/admin/shop")
@Api(tags = "卖家端商铺接口")
@Slf4j
public class ShopController {

    @Autowired
    RedisTemplate redisTemplate;

    public static final String KEY = "SHOP_STATUS";

    /**
     *
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("修改店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("当前店铺状态 "+ status);
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("返回店铺的运营状态")
    public Result<Integer> getStatus(){

        Integer status = (Integer)  redisTemplate.opsForValue().get(KEY);
        log.info("获取当前店铺状态关闭 " +  status);
        return Result.success(status);
    }

}
