package com.fire;

import com.fire.dao.BrandMapper;
import com.fire.dao.SpecMapper;
import com.fire.pojo.goods.Category;
import com.fire.service.goods.CategoryService;
import com.fire.utils.CacheKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fire
 * @date 2021年06月07日21:42
 */
@ContextConfiguration(locations = "classpath*:application*.xml")
@RunWith(SpringJUnit4ClassRunner.class)


public class test {

    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private SpecMapper specMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CategoryService categoryService;

    @Test
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

    @Test
    public void testGetData() {
        Map map = redisTemplate.boundHashOps(CacheKey.BRAND).entries();
        Map mapAll = (Map) map.get("手机");
        List<Map> listByCategoryName = (List<Map>) mapAll.get("brand");
        for (Map map1 : listByCategoryName) {
            System.out.println(map1.get("name"));
            System.out.println(map1.get("image"));
        }
    }
}
