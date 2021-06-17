package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.Result;
import com.fire.pojo.user.User;
import com.fire.service.user.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


/**
 * @author fire
 * @date 2021年06月13日14:00
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    @GetMapping("/sendSms")
    public Result sendSms(String phone){
        userService.sendSms(phone);
        return new Result();
    }
    @PostMapping("/save")
    public Result save(@RequestBody User user, String smsCode){
//        密码加密
        String encode = new BCryptPasswordEncoder().encode(user.getPassword());
        user.setPassword(encode);
        System.out.println(smsCode);
        userService.add(user,smsCode);
        return new Result();

    }
}
