package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.Result;
import com.fire.pojo.order.Order;
import com.fire.pojo.user.Address;
import com.fire.service.order.CartService;
import com.fire.service.order.OrderService;
import com.fire.service.user.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fire
 * @date 2021年06月17日20:57
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    /**
     * 根据用户名查找购物车的信息
     * @return
     */
    @GetMapping("/findCartList")
    public List<Map<String,Object>>findCardList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map<String, Object>> cartList = cartService.findCartList(username);
        return cartList;

    }

    /**
     * 将购物车中的货品实现添加或者减少，还有删除
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/addItem")
    public Result addItem(String skuId,Integer num){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username,skuId,num);
        return new Result();
    }

    /**
     * 从商品信息中加入购物车
     * @param response
     * @param skuId
     * @param num
     * @throws IOException
     */
    @GetMapping("/buy")
    public void buy(HttpServletResponse response,String skuId,Integer num) throws IOException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.addItem(username,skuId,num);
        response.sendRedirect("/cart.html");
    }

    /**
     * 保存购物车中的是否选中状态信息
     * @param skuId
     * @param checked
     * @return
     */
    @GetMapping("/updateChecked")
    public Result updateChecked(String skuId,boolean checked){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.updateChecked(username,skuId,checked);
        return new Result();
    }

    /**
     * 删除选中的购物车列表
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        cartService.deleteCheckedCart(username);
        return new Result();
    }


    /**
     * 计算购物车优惠金额
     * @return
     */
    @GetMapping("/preferential")
    public Map preferential(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        int preferenial = cartService.preferenial(username);
        Map map = new HashMap();
        map.put("preferential",preferenial);
        return map;
    }

    /**
     * 获得刷新购物车单价的方法
     * @return
     */
    @GetMapping("/findNewOrderItemList")
    public List<Map<String,Object>>findNewOrderItemList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartService.findNewOrderList(username);
    }

    @Reference
    private AddressService addressService;

    /**
     * 返回当前用户的地址列表
     * @return
     */
    @GetMapping("/findByAddress")
    public List<Address> findByAddress(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findByUsername(username);
    }

    @Reference
    private OrderService orderService;

    /**
     * 保存订单
     * @param order
     * @return
     */
    @PostMapping("/saveOrder")
    public Map<String,Object>saveOrder(@RequestBody Order order){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUsername(username);
        return orderService.add(order);

    }


}
