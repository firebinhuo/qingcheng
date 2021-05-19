package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fire.dao.OrderConfigMapper;
import com.fire.dao.OrderItemMapper;
import com.fire.dao.OrderLogMapper;
import com.fire.pojo.order.*;
import com.fire.utils.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.fire.dao.OrderMapper;
import com.fire.entity.PageResult;
import com.fire.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    /**
     * 组合Order和OrderItem
     */
    @Autowired
    private OrderItemMapper orderItemMapper;

    /**
     * 超时订单处理实现类
     * 注入订单日志mapper
     */
    @Autowired
    private OrderConfigMapper orderConfigMapper;

    /**
     * 返回全部记录
     *
     * @return order集合
     */
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Order> orders = (Page<Order>) orderMapper.selectAll();
        return new PageResult<>(orders.getTotal(), orders.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return List<Order>
     */
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap 拆寻条件map
     * @param page      第几页
     * @param size      页大小
     * @return order集合
     */
    public PageResult<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Order> orders = (Page<Order>) orderMapper.selectByExample(example);
        return new PageResult<>(orders.getTotal(), orders.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id id
     * @return order
     */
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param order 订单
     */
    public void add(Order order) {
        orderMapper.insert(order);
    }

    /**
     * 修改
     *
     * @param order 订单
     */
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public OrderFull findOrderFullById(String id) {
//        组合实体类
//        根据Order主表ID找到order，orderItem
//        组合得到的信息并返回
        OrderFull orderFull = new OrderFull();

        Order order = orderMapper.selectByPrimaryKey(id);
        OrderItem orderItem = orderItemMapper.selectByPrimaryKey(order.getId());
        orderFull.setOrder(order);
        orderFull.setOrderItem(orderItem);

        return orderFull;

    }

    @Autowired
    private OrderLogMapper logMapper;

    @Transactional
    @Override
    public void batchSend(List<Order> orders) {
        //判断运单号和物流公司是否为空
        for (Order order : orders) {
            if (order.getShippingCode() == null ||
                    order.getShippingName() == null) {
                throw new RuntimeException("请选择快递公司和填写快递单号");
            }
        }
        OrderLog orderLog = new OrderLog();
        for (Order order : orders) {
            order.setOrderStatus("2");//订单状态，已发货
            order.setConsignStatus("1");//发货状态，已发货
            order.setConsignTime(new Date());//发货时间
            orderMapper.updateByPrimaryKeySelective(order);

            // TODO: 2021/5/18 添加订单日志
            long l = new IdWorker().nextId();
            orderLog.setId(l + "");
            orderLog.setOrderId(order.getId());
            orderLog.setOrderStatus(order.getOrderStatus());
            orderLog.setConsignStatus(order.getConsignStatus());
            orderLog.setOperater("admin");
            orderLog.setOperateTime(new Date());
            orderLog.setPayStatus(order.getPayStatus());
            orderLog.setRemarks("合作愉快");

            logMapper.insert(orderLog);

        }
    }

    /**
     * 删除
     *
     * @param id id
     */
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void orderTimeLogic() {
        OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey(1);
        Integer orderTimeout = orderConfig.getOrderTimeout();//超时时间
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(orderTimeout);//得到超时的超时时间点
//        设置查询条件
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("createTime", localDateTime);//创建时间小于超时时间
        criteria.andEqualTo("orderStatus", "0");//未付款的
        criteria.andEqualTo("isDelete", "0");//未删除的

//        查询超时订单
        List<Order> orders = orderMapper.selectByExample(example);
        for (Order order : orders) {
            //记录订单变动日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(new IdWorker().nextId() + "");
            orderLog.setOperater("system");// 系统
            orderLog.setOperateTime(new Date());//当前日期
            orderLog.setOrderStatus("4");
            orderLog.setPayStatus(order.getPayStatus());
            orderLog.setConsignStatus(order.getConsignStatus());
            orderLog.setRemarks("超时订单，系统自动关闭");
            orderLog.setOrderId(order.getId());
            logMapper.insert(orderLog);
            //更改订单状态
            order.setOrderStatus("4");
            order.setCloseTime(new Date());//关闭日期
            orderMapper.updateByPrimaryKeySelective(order);
        }


    }

    /**
     * 构建查询条件
     *
     * @param searchMap map
     * @return example查询条件
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andLike("id", "%" + searchMap.get("id") + "%");
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andLike("payType", "%" + searchMap.get("payType") + "%");
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andLike("sourceType", "%" + searchMap.get("sourceType") + "%");
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andLike("orderStatus", "%" + searchMap.get("orderStatus") + "%");
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andLike("payStatus", "%" + searchMap.get("payStatus") + "%");
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andLike("consignStatus", "%" + searchMap.get("consignStatus") + "%");
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andLike("isDelete", "%" + searchMap.get("isDelete") + "%");
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }
            //id数组
            if (searchMap.get("ids") != null) {
                System.out.println(Arrays.asList(searchMap.get("ids").toString()));
                System.out.println(searchMap.get("ids"));
                criteria.andIn("id", (JSONObject.parseArray(JSON.toJSONString(searchMap.get("ids")))));
            }

        }
        return example;
    }

}
