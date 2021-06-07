package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fire.pojo.goods.Category;
import com.fire.pojo.goods.Goods;
import com.fire.pojo.goods.Sku;
import com.fire.pojo.goods.Spu;
import com.fire.service.goods.CategoryService;
import com.fire.service.goods.SpuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/item")
@RestController
public class ItemController {
    @Reference
    private SpuService spuService;

    @Value("${pagePath}")
    private String pagePath;

    @Reference
    private CategoryService categoryService;

    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/createPage")
    public void createPage(String spuId) {//根据spuId批量生产sku页面
//        1.查询商品信息
        Goods goods = spuService.findGoodsById(spuId);
        Spu spu = goods.getSpu();//获取spu信息
//       获取sku列表
//        查询商品分类
        List<String> categoryList = new ArrayList<>();
        categoryList.add(categoryService.findById(spu.getCategory1Id()).getName());//一级分类
        categoryList.add(categoryService.findById(spu.getCategory2Id()).getName());//二级分类
        categoryList.add(categoryService.findById(spu.getCategory3Id()).getName());//三级分类
        List<Sku> skuList = goods.getSkuList();

//       遍历每一个sku，得到url,即sku地址列表
        Map<String, String> urlMap = new HashMap<>();
        for (Sku sku : skuList) {
            if ("1".equals(sku.getStatus())) {
                String specJson = JSON.toJSONString(JSON.parseObject(sku.getSpec()), SerializerFeature.MapSortField);
                urlMap.put(specJson, sku.getId() + ".html");
            }

        }

//        2.迭代生成页面
        for (Sku sku : skuList) {
//            (1):创建上下文和数据模型
            Context context = new Context();
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("spu", spu);
            dataModel.put("sku", sku);
            dataModel.put("categoryList", categoryList);//实现面包屑菜单栏
            dataModel.put("skuImages", sku.getImages().split(","));
            dataModel.put("spuImages", spu.getImages().split(","));

            Map paraItems = JSON.parseObject(spu.getParaItems());//参数列表
            dataModel.put("paraItems", paraItems);

            Map<String, String> specItems = (Map) JSON.parseObject(sku.getSpec()); //当前sku
            dataModel.put("specItems", specItems);//规格列表
//           {"颜色":["深色","黑色","蓝色"],"尺码":["27","28","29"]}
//            {"颜色":[{'option':"深色",checked:true},{'option':"黑色",checked:false}],"尺码":["27","28","29"]}
            Map<String, List> specMap = (Map) JSON.parseObject(spu.getSpecItems());//规格和规格选项数据
//           循环map集合，改造数据
            for (String key : specMap.keySet()) {
                List<String> list = specMap.get(key);//{["深色","黑色","蓝色"],["27","28","29"]}
                List<Map> mapList = new ArrayList<>();//新的集合  {"颜色":[{'option':"深色",checked:true},{'option':"黑色",checked:false}],"尺码":["27","28","29"]}
                for (String s1 : list) {//循环规格选项
                    Map map = new HashMap();
                    map.put("option", s1);//规格选项
                    if (specItems.get(key).equals(s1)) {
                        map.put("checked", true);
                    } else {
                        map.put("checked", false);
                    }

                    Map<String, String> spec = (Map) JSON.parseObject(sku.getSpec());//当前的sku{'颜色': '红色', '版本': '8GB+128GB'}
                    spec.put(key, s1);

                    String specJson = JSON.toJSONString(spec, SerializerFeature.MapSortField);
                    map.put("url", urlMap.get(specJson));//

                    mapList.add(map);


                }
                specMap.put(key, mapList);
            }

            dataModel.put("specMap", specMap);//用新的集合替换原有的集合

            context.setVariables(dataModel);
//            (2):准备文件
            File dir = new File(pagePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dir, sku.getId() + ".html");
//            (3)生成页面
            try {
                PrintWriter writer = new PrintWriter(dest, "UTF-8");
                templateEngine.process("item", context, writer);
                System.out.println("生成页面" + sku.getId() + ".html");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }
}
