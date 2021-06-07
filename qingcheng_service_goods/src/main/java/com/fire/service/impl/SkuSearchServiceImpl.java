package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fire.dao.BrandMapper;
import com.fire.dao.SpecMapper;
import com.fire.service.goods.SkuSearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fire
 * @date 2021年06月06日16:56
 */
@Service
public class SkuSearchServiceImpl implements SkuSearchService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpecMapper specMapper;

    /**
     * @param searchMap 1.封装查询请求
     *                  2.获得查询结果
     */
    @Override
    public Map search(Map<String, String> searchMap) {
        SearchRequest searchRequest = new SearchRequest("sku");
        searchRequest.types("doc");//设置查询的类型

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//{"query":}
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();//布尔构建查询器
//        1.1关键字搜索
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("name", searchMap.get("keyword"));
        boolQueryBuilder.must(matchQueryBuilder);
//      1.2商品分类过滤
        if (searchMap.get("category") != null) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("categoryName", searchMap.get("category"));
            boolQueryBuilder.filter(termQueryBuilder);
        }
//       1.3品牌过滤
        if (searchMap.get("brand") != null) {
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("brandName", searchMap.get("brand"));
            boolQueryBuilder.filter(termQueryBuilder);
        }
//        1.4规格过滤
        for (String key : searchMap.keySet()) {
            if (key.startsWith("spec.")) {//如果是规格参数
                TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(key + ".keyword", searchMap.get(key));
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }
//        1.5价格筛选
        if (searchMap.get("price") != null) {
            String[] priceAtoB = ((String) searchMap.get("price")).split("-");
            if (!priceAtoB[0].equals(0)) {//最低价格不等于零
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(priceAtoB[0] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);
            }
            if (!priceAtoB[1].equals("*")) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price").lt(priceAtoB[1] + "00");
                boolQueryBuilder.filter(rangeQueryBuilder);

            }

        }

        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);

//        聚合查询
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("sku_category").field("categoryName");
        searchSourceBuilder.aggregation(termsAggregationBuilder);
//        2 查询结果
        Map resultMap = new HashMap();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] hits = searchHits.getHits();
            List<Map<String, Object>> resultList = new ArrayList<>();
            //2.1 商品列表查询
            for (SearchHit hit : hits) {
                Map<String, Object> skuMap = hit.getSourceAsMap();
                resultList.add(skuMap);
            }
            resultMap.put("rows", resultList);
//          2.2商品分类列表分装
            Aggregations aggregations = searchResponse.getAggregations();
            Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
            Terms terms = (Terms) aggregationMap.get("sku_category");
            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            List<String> categoryList = new ArrayList();
            for (Terms.Bucket bucket : buckets) {
                categoryList.add(bucket.getKeyAsString());
            }
            resultMap.put("categoryList", categoryList);


            String categoryName = "";
            if (searchMap.get("category") == null) {//如果没有分类条件
                if (categoryList.size() > 0) {
                    categoryName = categoryList.get(0);//提取第一个分类
                }
            } else {
                categoryName = searchMap.get("category");//去除参数中的分类
            }
            //2.3品牌列表
            if (searchMap.get("brand") == null) {

                List<Map> brandList = brandMapper.findListByCategoryName(categoryName);//查询品牌列表
                resultMap.put("brandList", brandList);
            }
//            2.4规格列表

            List<Map> specList = specMapper.findSpecByCategoryName(categoryName);//查询规格列表

            for (Map spec : specList) {
                String[] options = ((String) spec.get("options")).split(",");
                spec.put("options", options);
            }
            resultMap.put("specList", specList);
//            if (searchMap.get("spec")==null){
//
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultMap;

    }
}
