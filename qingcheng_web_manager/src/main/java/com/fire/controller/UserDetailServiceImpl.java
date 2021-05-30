package com.fire.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.pojo.system.Admin;
import com.fire.service.system.AdminService;
import com.fire.service.system.ResourceService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserDetailServiceImpl implements UserDetailsService {
    @Reference
    private AdminService adminService;

    @Reference
    private ResourceService resourceService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {


        System.out.println("经过GrantedAuthority认证");
        Map map = new HashMap<>();
        map.put("loginName", s);
        map.put("status", 1);

        List<Admin> adminServiceList = adminService.findList(map);
        if (adminServiceList.size() == 0) {
            return null;
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        List<String> resKeyList = resourceService.findResKeyByLoginName(s);
        for (String resKey : resKeyList) {
            System.out.println(resKey);
            grantedAuthorities.add(new SimpleGrantedAuthority(resKey));
        }
//        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        grantedAuthorities.add(new SimpleGrantedAuthority("goods_edit"));
        return new User(s, adminServiceList.get(0).getPassword(), grantedAuthorities);

    }
}
