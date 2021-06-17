package com.fire.consumer;

import com.alibaba.fastjson.JSON;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author fire
 * @date 2021年06月13日15:16
 */
@Component
public class SmsMessageConsumer implements MessageListener {
    @Autowired
    private SmsUtil smsUtil;
    @Value("${smsCode}")
    private String smsCode;

    @Value("${param}")
    private String param;

    @Override
    public void onMessage(Message message) {
        String jsonString = new String(message.getBody());
        Map <String,String  >map = JSON.parseObject(jsonString, Map.class);
        String phone = map.get("phone");//手机号
        String code = map.get("code");//验证码

        System.out.println("phone: "+phone + "\n code" + code);
        // TODO: 2021/6/13 fire 调用阿里云通信
//        smsUtil.sendSms(phone,smsCode,param.replace("[value]",code));//发送短信验证码
    }
}
