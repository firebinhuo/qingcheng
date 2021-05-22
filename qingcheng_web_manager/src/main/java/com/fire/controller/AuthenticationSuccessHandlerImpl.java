package com.fire.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.pojo.system.LoginLog;
import com.fire.service.system.LoginLogService;
import com.fire.utils.WebUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Reference
    private LoginLogService loginLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        //登录成功后，会调用日志   转发到主页面
        httpServletRequest.getRequestDispatcher("/main.html").forward(httpServletRequest, httpServletResponse);
        LoginLog loginLog = new LoginLog();


        String ip = httpServletRequest.getRemoteAddr();//ip
        String agent = httpServletRequest.getHeader("user-agent");//浏览器名称
        String browserName = WebUtil.getBrowserName(agent);


        loginLog.setLoginName(authentication.getName());//当前登录用户
        loginLog.setLoginTime(new Date());//登录时间
        loginLog.setLocation(WebUtil.getCityByIP(ip));//根据IP算出地址
        loginLog.setBrowserName(browserName);//浏览器名称
        loginLog.setIp(ip);//ip
        loginLogService.add(loginLog);
    }
}
