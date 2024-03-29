package com.fire.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.PageResult;
import com.fire.entity.Result;
import com.fire.pojo.order.Order;
import com.fire.pojo.order.OrderFull;
import com.fire.service.order.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @GetMapping("/findAll")
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Order> findPage(int page, int size) {
        return orderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Order> findList(@RequestBody Map<String, Object> searchMap) {
        System.out.println(searchMap);
        return orderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Order> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return orderService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public Order findById(String id) {
        return orderService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Order order) {
        orderService.add(order);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Order order) {
        orderService.update(order);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id) {
        orderService.delete(id);
        return new Result();
    }

    @GetMapping("/orderFull")
    public OrderFull findOrderFullById(String id) {
        return orderService.findOrderFullById(id);
    }


    @PostMapping("/batchSend")
    public Result batchSend(@RequestBody List<Order> orders) {
        System.out.println(orders);
        orderService.batchSend(orders);
        return new Result();

    }

}
