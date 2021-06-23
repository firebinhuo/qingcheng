package com.fire.service.order;

import java.util.List;
import java.util.Map;

/**
 * @author fire
 * @date 2021年06月17日20:46
 */
public interface CartService {

    /**
     * 从redis中提取某用户的购物车
     * @return
     */
    public List<Map<String ,Object>>findCartList(String username);

    /**
     * 添加商品到购物车
     * @param username  用户名
     * @param skuId 商品id
     * @param num 商品数量
     */
    public void addItem(String username, String skuId,Integer num);

    /**
     * 保存购物车勾选状态
     * @param username 用户名
     * @param skuId 商品ID
     * @param checked 勾选状态
     * @return
     */
    public boolean updateChecked(String username, String skuId, boolean checked);

    /**
     * 删除选中的商品
     */
    public void deleteCheckedCart(String username);

    /**
     * 计算购物车优惠金额
     * @param username
     * @return
     */
    public int preferenial(String username);

    /**
     * 获得最新购物车列表
     * @param username
     * @return
     */
    public List<Map<String,Object>>findNewOrderList(String username);
}
