package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.service.goods.SkuSearchService;
import com.fire.utils.WebUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.fire.entity.Result;
import org.springframework.web.bind.annotation.RequestParam;
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

//        如果没有页码，默认为    1
        if (map.get("pageNo") == null) {
            map.put("pageNo", "1");
        }

        //页面传递给后端两个数据 sort:排序字段 sortOrder:排序规则
        if (map.get("sort")==null){//排序字段
            map.put("sort","");
        }
        if (map.get("sortOrder")==null){//排序规则
            map.put("sortOrder","DESC");
        }
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

//        页码
        model.addAttribute("searchMap", map);
        int pageN = Integer.parseInt(map.get("pageNo"));
        model.addAttribute("pageNo", pageN);
//       页码处理
        long totalPages = (long) search.get("totalPages");//总页数
        int startPage = 1;//开始页码
        long endPage = totalPages;//截止页码
        if (totalPages > 5) {
            startPage = pageN - 2;
            if (startPage < 1) {
                startPage = 1;
            }
            endPage = startPage + 4;
        }
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "search";
    }

}
