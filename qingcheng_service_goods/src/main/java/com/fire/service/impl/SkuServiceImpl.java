package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fire.pojo.order.OrderItem;
import com.fire.utils.CacheKey;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.fire.dao.SkuMapper;
import com.fire.entity.PageResult;
import com.fire.pojo.goods.Sku;
import com.fire.service.goods.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SkuService.class)
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuMapper skuMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Sku> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectAll();
        return new PageResult<Sku>(skus.getTotal(), skus.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Sku> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return skuMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Sku> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Sku> skus = (Page<Sku>) skuMapper.selectByExample(example);
        return new PageResult<Sku>(skus.getTotal(), skus.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Sku findById(String id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param sku
     */
    public void add(Sku sku) {
        skuMapper.insert(sku);
    }

    /**
     * 修改
     *
     * @param sku
     */
    public void update(Sku sku) {
        skuMapper.updateByPrimaryKeySelective(sku);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(String id) {
        skuMapper.deleteByPrimaryKey(id);
    }

    /**
     * 将sku价格缓存到redis中
     */
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveAllPriceToRedis() {
//        检查redis中是否存在sku_price
        if (!redisTemplate.hasKey(CacheKey.SKU_PRICE)) {
//            没有则加载所有的sku_price
            List<Sku> skuList = skuMapper.selectAll();
            List<Sku> skus = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Sku sku = skuList.get(i);
                skus.add(sku);
            }

            for (Sku sku : skus) {
                if ("1".equals(sku.getStatus())) {
                    redisTemplate.boundHashOps(CacheKey.SKU_PRICE).put(sku.getId(), sku.getPrice());
                }

            }
        } else {
            System.out.println("sku price is already exist!!!!");
        }

    }

    /***
     * 根据id查询价格实现类
     * @param id 查询id
     * @return 返回价格
     */
    @Override
    public Integer findPriceById(String id) {

        return (Integer) redisTemplate.boundHashOps(CacheKey.SKU_PRICE).get(id);

    }

    /**
     * 更具id查询价格
     *
     * @param id
     * @param price
     */
    @Override
    public void savePriceById(String id, Integer price) {

        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).put(id, price);//覆盖

    }

    @Override
    public void deletePriceFromRedis(String id) {
        redisTemplate.boundHashOps(CacheKey.SKU_PRICE).delete(id);
    }

//    @Transactional
    @Override
    public boolean deductionStock(List<OrderItem> orderItemList) {
//        1.检查是否可以扣减库存
        boolean isDuction = true;  //是否可以扣减
        for (OrderItem orderItem : orderItemList) {
            Sku sku = findById(orderItem.getSkuId());
            if (sku==null){
                isDuction = false;
                break;
            }
            if(!"1".equals(sku.getStatus())){
                isDuction = false;
                break;
            }
            if (sku.getNum().intValue()<orderItem.getNum().intValue()){
                isDuction = false;
                break;
            }
        }

//        2.执行扣减
        if (isDuction){
            for (OrderItem orderItem : orderItemList) {
                skuMapper.deductionStock(orderItem.getSkuId(),orderItem.getNum());//扣减库存
                skuMapper.addSaleNum(orderItem.getSkuId(),orderItem.getNum());//增加销量
            }
        }

        return isDuction;
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 商品id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 商品条码
            if (searchMap.get("sn") != null && !"".equals(searchMap.get("sn"))) {
                criteria.andLike("sn", "%" + searchMap.get("sn") + "%");
            }
            // SKU名称
            if (searchMap.get("name") != null && !"".equals(searchMap.get("name"))) {
                criteria.andLike("name", "%" + searchMap.get("name") + "%");
            }
            // 商品图片
            if (searchMap.get("image") != null && !"".equals(searchMap.get("image"))) {
                criteria.andLike("image", "%" + searchMap.get("image") + "%");
            }
            // 商品图片列表
            if (searchMap.get("images") != null && !"".equals(searchMap.get("images"))) {
                criteria.andLike("images", "%" + searchMap.get("images") + "%");
            }
            // SPUID
            if (searchMap.get("spuId") != null && !"".equals(searchMap.get("spuId"))) {
                criteria.andLike("spuId", "%" + searchMap.get("spuId") + "%");
            }
            // 类目名称
            if (searchMap.get("categoryName") != null && !"".equals(searchMap.get("categoryName"))) {
                criteria.andLike("categoryName", "%" + searchMap.get("categoryName") + "%");
            }
            // 品牌名称
            if (searchMap.get("brandName") != null && !"".equals(searchMap.get("brandName"))) {
                criteria.andLike("brandName", "%" + searchMap.get("brandName") + "%");
            }
            // 规格
            if (searchMap.get("spec") != null && !"".equals(searchMap.get("spec"))) {
                criteria.andLike("spec", "%" + searchMap.get("spec") + "%");
            }
            // 商品状态 1-正常，2-下架，3-删除
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andLike("status", "%" + searchMap.get("status") + "%");
            }

            // 价格（分）
            if (searchMap.get("price") != null) {
                criteria.andEqualTo("price", searchMap.get("price"));
            }
            // 库存数量
            if (searchMap.get("num") != null) {
                criteria.andEqualTo("num", searchMap.get("num"));
            }
            // 库存预警数量
            if (searchMap.get("alertNum") != null) {
                criteria.andEqualTo("alertNum", searchMap.get("alertNum"));
            }
            // 重量（克）
            if (searchMap.get("weight") != null) {
                criteria.andEqualTo("weight", searchMap.get("weight"));
            }
            // 类目ID
            if (searchMap.get("categoryId") != null) {
                criteria.andEqualTo("categoryId", searchMap.get("categoryId"));
            }
            // 销量
            if (searchMap.get("saleNum") != null) {
                criteria.andEqualTo("saleNum", searchMap.get("saleNum"));
            }
            // 评论数
            if (searchMap.get("commentNum") != null) {
                criteria.andEqualTo("commentNum", searchMap.get("commentNum"));
            }

        }
        return example;
    }

}
