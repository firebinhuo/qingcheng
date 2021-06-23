package com.fire.service.goods;

import com.fire.entity.PageResult;
import com.fire.pojo.goods.Sku;
import com.fire.pojo.order.OrderItem;

import java.util.*;

/**
 * sku业务逻辑层
 */
public interface SkuService {


    public List<Sku> findAll();


    public PageResult<Sku> findPage(int page, int size);


    public List<Sku> findList(Map<String, Object> searchMap);


    public PageResult<Sku> findPage(Map<String, Object> searchMap, int page, int size);


    public Sku findById(String id);

    public void add(Sku sku);


    public void update(Sku sku);


    public void delete(String id);

    public void saveAllPriceToRedis();

    /**
     * 根据sku_id查询价格
     *
     * @param id
     * @return
     */
    public Integer findPriceById(String id);

    /**
     * 根据ID，存入价格
     *
     * @param id
     * @param price
     */
    public void savePriceById(String id, Integer price);

    /**
     * 根据sku的id删除缓存
     *
     * @param id
     */
    public void deletePriceFromRedis(String id);

    /**
     * 批量扣减库存
     * @param orderItemList
     * @return
     */
    public boolean deductionStock(List<OrderItem> orderItemList);

}
