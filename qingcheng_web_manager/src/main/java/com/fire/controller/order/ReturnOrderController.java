package com.fire.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.PageResult;
import com.fire.entity.Result;
import com.fire.pojo.order.ReturnOrder;
import com.fire.service.order.ReturnOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/returnOrder")
public class ReturnOrderController {

    @Reference
    private ReturnOrderService returnOrderService;

    @GetMapping("/findAll")
    public List<ReturnOrder> findAll() {
        return returnOrderService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<ReturnOrder> findPage(int page, int size) {
        return returnOrderService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<ReturnOrder> findList(@RequestBody Map<String, Object> searchMap) {
        return returnOrderService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<ReturnOrder> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return returnOrderService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public ReturnOrder findById(Long id) {
        return returnOrderService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody ReturnOrder returnOrder) {
        returnOrderService.add(returnOrder);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody ReturnOrder returnOrder) {
        returnOrderService.update(returnOrder);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Long id) {
        returnOrderService.delete(id);
        return new Result();
    }

    /**
     * 同意退款
     */
    @GetMapping("/agreeRefund")
    public Result agreeRefund(String id, Integer money) {
        Integer adminId = 25013;
        returnOrderService.agreeRefund(id, money, adminId);
        return new Result();
    }

    /**
     * 拒绝退款
     */
    public Result refuseRefund(String id, String remark) {
        Integer adminId = 25013;
        returnOrderService.refuseRefund(id, remark, adminId);
        return new Result();
    }


}
