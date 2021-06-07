package com.fire.service.impl;

import com.fire.service.business.AdService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Init implements InitializingBean {
    @Autowired
    private AdService adService;

    @Override
    public void afterPropertiesSet() throws Exception {
//        缓存预热，将所有广告数据加入到redis中
        System.out.println("缓存预热");
        adService.saveAllAdToRedis();
    }
}
