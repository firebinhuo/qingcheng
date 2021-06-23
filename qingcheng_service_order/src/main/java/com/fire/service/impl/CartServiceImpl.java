package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.fire.pojo.goods.Category;
import com.fire.pojo.goods.Sku;
import com.fire.pojo.order.OrderItem;
import com.fire.service.goods.CategoryService;
import com.fire.service.goods.SkuService;
import com.fire.service.order.CartService;
import com.fire.service.order.PreferentialService;
import com.fire.utils.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fire
 * @date 2021年06月23日9:31
 */
@Service(interfaceClass = CartService.class)
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Map<String, Object>> findCartList(String username) {
        System.out.println("from redis get cartList" + username);
        List<Map<String, Object>> cartList = (List<Map<String, Object>>) redisTemplate.boundHashOps(CacheKey.CART_LIST).get(username);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        return cartList;
    }


    /**
     * 添加商品实现类
     *
     * @param username  用户名
     * @param skuId 商品id
     * @param num 商品数量
     */
    @Reference
    private SkuService skuService;//商品服务

    @Reference
    private CategoryService categoryService;//列表信息

    @Override
    public void addItem(String username, String skuId, Integer num) {
//        实现思路：
//        遍历购物车，如果商品中存在该商品则累加；如果不存在，则新增购物车项
        List<Map<String, Object>> cartList = findCartList(username);
        boolean flag = false;//是否在购物车存在
        for (Map<String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            if (orderItem.getSkuId().equals(skuId)) {//购物车存在该商品
                if (orderItem.getNum() <= 0) {//订单数量为0或者负数，则商品没有意义，从购物车中删除
                    cartList.remove(map);
                    flag = true;
                    break;
                }
                int weight = orderItem.getWeight() / orderItem.getNum();//单个商品重量
                orderItem.setNum(orderItem.getNum() + num);//数量变更
                orderItem.setMoney(orderItem.getPrice() * orderItem.getNum());//金额变更
                orderItem.setWeight(weight * orderItem.getNum());//重量变更
                flag = true;
                if (orderItem.getNum() <= 0) {//订单数量为0或者负数，则商品没有意义，从购物车中删除
                    cartList.remove(map);
                }
                break;
            }
        }
        if (flag == false) {//如果购物车中没有则添加
            Sku sku = skuService.findById(skuId);
            if (sku == null) {
                throw new RuntimeException("sku数据为空，商品不存在");
            }
            if ("q".equals(sku.getStatus())) {
                throw new RuntimeException("商品状态不合法");
            }
            if (num <= 0) {
                throw new RuntimeException("商品数量不合法");
            }
            OrderItem orderItem = new OrderItem();
            orderItem.setSkuId(skuId);
            orderItem.setSpuId(sku.getSpuId());
            orderItem.setImage(sku.getImage());
            orderItem.setPrice(sku.getPrice());
            orderItem.setMoney(sku.getPrice() * num);
            orderItem.setNum(num);//总金额计算
            if (sku.getWeight() == null) {
                sku.setWeight(0);
            }
            orderItem.setWeight(sku.getWeight() * num);//总重量计算
//            商品分类
            orderItem.setCategoryId3(sku.getCategoryId());
            Category category3 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(sku.getCategoryId());//先去缓存查
            if (category3 == null) {
                category3 = categoryService.findById(sku.getCategoryId());//根据id查询三级分类
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(sku.getCategoryId(), category3);
            }
            orderItem.setCategoryId2(category3.getParentId());
            Category category2 = (Category) redisTemplate.boundHashOps(CacheKey.CATEGORY).get(category3.getParentId());//先去缓存查
            if (category2 == null) {
                category2 = categoryService.findById(category3.getParentId());//根据三级分类查二级菜单
                redisTemplate.boundHashOps(CacheKey.CATEGORY).put(category3.getParentId(), category2);
            }
            orderItem.setCategoryId1(category2.getParentId());
            Map map = new HashMap();
            map.put("item", orderItem);
            cartList.add(map);


        }
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
    }

    @Override
    public boolean updateChecked(String username, String skuId, boolean checked) {
        List<Map<String, Object>> cartList = findCartList(username);
        boolean isOk = false;
        for (Map<String, Object> map : cartList) {
            OrderItem orderItem = (OrderItem) map.get("item");
            if (orderItem.getSkuId().equals(skuId)) {
                map.put("checked", checked);
                isOk = true;
                break;
            }
        }
        if (isOk) {
            redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
        }
        return isOk;
    }

    @Override
    public void deleteCheckedCart(String username) {
        List<Map<String, Object>> uncheckedList = findCartList(username).stream().filter(cart -> (boolean) cart.get("checked") == false).collect(Collectors.toList());
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, uncheckedList);
    }

    @Autowired
    private PreferentialService preferentialService;

    @Override
    public int preferenial(String username) {
        /**
         * 1.获取选中的购物车   List<OrderItem></>
         * 2.按分类统计每个分类的金额
         * 3.循环结果，统计每个分类的的优惠金额，并累加
         */
        List<OrderItem> orderItemList = findCartList(username)
                .stream()
                .filter(cart -> (boolean) cart.get("checked") == true)
                .map(cart -> (OrderItem) cart.get("item"))
                .collect(Collectors.toList());

        //2.  分类    金额
        //      1      500
        //      2      100
        Map<Integer, IntSummaryStatistics> cartMap = orderItemList
                .stream()
                .collect(
                        Collectors
                                .groupingBy(
                                        OrderItem::getCategoryId3,
                                        Collectors.summarizingInt(OrderItem::getMoney)
                                )
                );
//        3
        int allPreMoney = 0;//累计优惠金额
        for (Integer categoryId : cartMap.keySet()) {
//            获取品类的消费金额
            int sum = (int) cartMap.get(categoryId).getSum();

            int preMoney = preferentialService.findPreMoneyByCategoryId(categoryId, sum);//获取优惠金额
            allPreMoney += preMoney;
        }
        System.out.println("category money " + allPreMoney);
        return allPreMoney;
    }


    @Override
    public List<Map<String, Object>> findNewOrderList(String username) {
//        1.获得购物车列表
        List<Map<String, Object>> cartList = findCartList(username);
//        2.循环购物车，刷新列表
        for (Map<String, Object> cart : cartList) {
            OrderItem orderItem = (OrderItem) cart.get("item");
            Sku sku = skuService.findById(orderItem.getSkuId());
            orderItem.setPrice(sku.getPrice());//更新价格
            orderItem.setMoney(sku.getPrice() * orderItem.getNum());//更新金额
        }
//        3.保存最新购物车
        redisTemplate.boundHashOps(CacheKey.CART_LIST).put(username, cartList);
        return cartList;
    }


}
