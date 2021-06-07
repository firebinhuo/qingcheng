package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.service.goods.SkuService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sku")
@CrossOrigin//跨域请求  要从
public class SkuController {
    @Reference
    private SkuService skuService;

    @GetMapping("/price")
    public Integer findPriceById(String id) {
        return skuService.findPriceById(id);
    }

}
