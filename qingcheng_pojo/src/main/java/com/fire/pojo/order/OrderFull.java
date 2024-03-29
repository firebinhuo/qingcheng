package com.fire.pojo.order;

import java.io.Serializable;

public class OrderFull implements Serializable {
    private Order order;

    private OrderItem orderItem;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
