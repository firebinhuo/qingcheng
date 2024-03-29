package com.fire.service.impl;

import com.fire.service.goods.CategoryService;
import com.fire.service.goods.SkuService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Init implements InitializingBean {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuService skuService;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("******缓存预热*******cache");
        categoryService.saveCategoryTreeToRedis();
        System.out.println("put sku_price to redis cache");
        skuService.saveAllPriceToRedis();
    }
}
