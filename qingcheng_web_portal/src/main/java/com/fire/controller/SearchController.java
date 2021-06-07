package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.service.goods.SkuSearchService;
import com.fire.utils.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author fire
 * @date 2021年06月06日17:13
 */
@Controller
//@RequestMapping("/search")
public class SearchController {
    @Reference
    private SkuSearchService skuSearchService;

    @GetMapping("/search")
    public String search(Model model, @RequestParam Map<String, String> map) throws Exception {
//        字符集处理(解决中文乱码问题)
        map = WebUtil.convertCharsetToUTF8(map);
        Map search = skuSearchService.search(map);
//        List<Map> rows = (List<Map>) search.get("rows");
//        for (Map row : rows) {
//            System.out.println(row.get("brandName"));
//        }
        model.addAttribute("result", search);

//        url处理
        StringBuilder url = new StringBuilder("/search.do?");
        for (String s : map.keySet()) {
            url.append("&" + s + "=" + map.get(s));
        }
        model.addAttribute("url", url);

        model.addAttribute("searchMap", map);
        return "search";
    }

}
