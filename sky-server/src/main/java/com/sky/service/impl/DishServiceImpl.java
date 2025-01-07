package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);


        dishMapper.insert(dish);

        // 获取菜品的id
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors!=null||flavors.size()>0){
            //向口味表dish_flavor插入n条
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO){
        //select * from dish limit 10,20
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }


    /**
     * 根据id批量删除菜品
     * @param ids
     * @return
     */
    @Override
    public void deleteBatch(List<Long> ids){

        ids.forEach(id->{
            //检查status是否是启用中
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                }
            }
        );

        //被套餐关联的菜品不能删除
        List<Long> setMealDishList = setmealDishMapper.getSetmealIdsByDishIds(ids);

        if (setMealDishList==null||setMealDishList.size()>0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }


        //删除菜品后，关联的口味数据也需要删除掉
        ids.forEach(id->{
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
            }
        );
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {

        //根据id获得菜品信息
        Dish dish = dishMapper.getById(id);

        //根据dishid获得口味信息
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);

        //将查询到的数据封装到vo
        DishVO dishVO = new DishVO();

        BeanUtils.copyProperties(dish,dishVO);

        dishVO.setFlavors(flavors);

        return dishVO;
    }

    /**
     *  *
     *  根据id修改菜品和关联的口味
     *  * @param dishDTO
     *  */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO,dish);

        Long id = dish.getId();

        List<DishFlavor> dishFlavors = dishDTO.getFlavors();


        dishFlavorMapper.deleteByDishId(id);

        if(dishFlavors!=null||dishFlavors.size()>0){
            //向口味表dish_flavor插入n条
            dishFlavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(dishFlavors);
        }

        dishMapper.update(dish);
    }


    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        ArrayList<DishVO> dishVOArrayList = new ArrayList<>();

        dishList.forEach(d->{
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d, dishVO);

            //  根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOArrayList.add(dishVO);
        });

        return dishVOArrayList;
    }

    @Override
    public void startOrStop(Integer status, Long id) {


        // 更新菜品的状态根据id
        Dish dish = new Dish().builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }


}
