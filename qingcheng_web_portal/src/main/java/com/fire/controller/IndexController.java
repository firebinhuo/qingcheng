package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.pojo.business.Ad;
import com.fire.service.business.AdService;
import com.fire.service.goods.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;


@Controller
public class IndexController {

    @Reference
    private AdService adService;

    @Reference
    private CategoryService categoryService;

    @GetMapping("/index")
    public String index(Model model) {
//       得到首页广告轮播图列表
        List<Ad> index_lb = adService.findBYPosition("web_index_lb");
        for (Ad ad : index_lb) {
            System.out.println(ad.getName());
        }
        model.addAttribute("lbt", index_lb);


        //分类导航
        List<Map> categoryTree = categoryService.findCategoryTree();
        model.addAttribute("categoryList", categoryTree);

        return "index";
    }
}
