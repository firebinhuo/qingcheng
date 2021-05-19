package com.fire.service.order;

import com.fire.entity.PageResult;
import com.fire.pojo.order.Order;
import com.fire.pojo.order.OrderFull;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    List<Order> findAll();


    PageResult<Order> findPage(int page, int size);


    List<Order> findList(Map<String, Object> searchMap);


    PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size);


    Order findById(String id);

    void add(Order order);


    void update(Order order);


    void delete(String id);

    OrderFull findOrderFullById(String id);

    /**
     * 批量发货
     *
     * @param orders 订单列表
     */
    void batchSend(List<Order> orders);

    /**
     * 超时订单处理逻辑
     */
    void orderTimeLogic();
}
