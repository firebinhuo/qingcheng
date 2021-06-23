package com.fire.consumer;

import com.alibaba.fastjson.JSON;
import com.fire.pojo.order.OrderItem;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author fire
 * @date 2021年06月21日19:05
 */

public class BackMessageConsumer implements MessageListener {

//    @Autowired
//    private StockBackService stockBackService;

    @Override
    public void onMessage(Message message) {
        try {
            //        提取消息
            String jsonStrin = new String(message.getBody());

            List<OrderItem> orderItemList = JSON.parseArray(jsonStrin, OrderItem.class);
//            stockBackService.addList(orderItemList);
        } catch (Exception e){
            e.printStackTrace();
//            记录日志，人工干预
        }

    }
}
