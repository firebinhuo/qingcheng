package com.fire.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fire
 * @date 2021年06月16日17:24
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    /**
     * @Description: 获取当前登录名
     * @Author: fire
     * @Date: 2021/6/16 17:28
     */
    @GetMapping("/username")
    public Map username(){

        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        System.out.println(user);
        if ("anonymousUser".equals(user)){
          user = "";
        }
        Map map = new HashMap();
        map.put("username",user);
        return map;
    }
}
