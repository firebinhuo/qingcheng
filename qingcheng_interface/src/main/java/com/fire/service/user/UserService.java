package com.fire.service.user;

import com.fire.entity.PageResult;
import com.fire.pojo.user.User;

import java.util.*;

/**
 * user业务逻辑层
 */
public interface UserService {


    public List<User> findAll();


    public PageResult<User> findPage(int page, int size);


    public List<User> findList(Map<String, Object> searchMap);


    public PageResult<User> findPage(Map<String, Object> searchMap, int page, int size);


    public User findById(String username);

    public void add(User user);


    public void update(User user);


    public void delete(String username);

    /**
     * 根据手机号，发送短信验证码
     * @param phone
     */
    public void sendSms(String phone);

    public void add(User user,String smsCode);

}
