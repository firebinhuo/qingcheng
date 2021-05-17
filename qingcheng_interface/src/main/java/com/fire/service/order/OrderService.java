package com.fire.service.order;

import com.fire.entity.PageResult;
import com.fire.pojo.order.Order;
import com.fire.pojo.order.OrderFull;

import java.util.*;

/**
 * order业务逻辑层
 */
public interface OrderService {


    public List<Order> findAll();


    public PageResult<Order> findPage(int page, int size);


    public List<Order> findList(Map<String, Object> searchMap);


    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size);


    public Order findById(String id);

    public void add(Order order);


    public void update(Order order);


    public void delete(String id);

    public OrderFull findOrderFullById(String id);

    /**
     * 批量发货
     *
     * @param orders
     */
    public void batchSend(List<Order> orders);
}
