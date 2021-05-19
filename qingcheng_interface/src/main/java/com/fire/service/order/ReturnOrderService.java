package com.fire.service.order;

import com.fire.entity.PageResult;
import com.fire.pojo.order.ReturnOrder;

import java.util.*;

/**
 * returnOrder业务逻辑层
 */
public interface ReturnOrderService {


    List<ReturnOrder> findAll();


    PageResult<ReturnOrder> findPage(int page, int size);


    List<ReturnOrder> findList(Map<String, Object> searchMap);


    PageResult<ReturnOrder> findPage(Map<String, Object> searchMap, int page, int size);


    ReturnOrder findById(Long id);

    void add(ReturnOrder returnOrder);


    void update(ReturnOrder returnOrder);


    void delete(Long id);

    /**
     * 同意退款
     */
    void agreeRefund(String id, Integer money, Integer adminId);

    void refuseRefund(String id, String remark, Integer adminId);
}
