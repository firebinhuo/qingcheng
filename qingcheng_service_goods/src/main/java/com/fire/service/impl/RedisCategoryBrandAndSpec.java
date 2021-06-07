package com.fire.service.impl;

import com.fire.dao.BrandMapper;
import com.fire.dao.CategoryMapper;
import com.fire.dao.SpecMapper;
import com.fire.pojo.goods.Category;
import com.fire.pojo.goods.Sku;
import com.fire.service.goods.CategoryService;
import com.fire.utils.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author fire
 * @date 2021年06月07日21:23
 */
@Component
public class RedisCategoryBrandAndSpec {
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private CategoryService categoryService;

    @Scheduled(cron = "0 0 0/24 * * ?")
    public void orderTimeOutLogic() {
        List<Category> categoryList = categoryService.findAll();
        for (Category category : categoryList) {
            if (!redisTemplate.hasKey(category.getName())) {
                List<Map> listByCategoryName = brandMapper.findListByCategoryName(category.getName());
                List<Map> spec = specMapper.findSpecByCategoryName(category.getName());
                Map<String, List> mapAll = new HashMap();
                mapAll.put("brand", listByCategoryName);
                mapAll.put("spec", spec);
                redisTemplate.boundHashOps(CacheKey.BRAND).put(category.getName(), mapAll);
            } else {
                System.out.println("brand and spec is already exist!!!!");
            }
        }


        System.out.println("每两分钟执行一次任务" + new Date());
//        orderService.orderTimeLogic();//定时添加
    }
}
