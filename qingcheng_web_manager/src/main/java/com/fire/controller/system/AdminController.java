package com.fire.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.PageResult;
import com.fire.entity.Result;
import com.fire.pojo.system.Admin;
import com.fire.pojo.system.AdminRole;
import com.fire.pojo.system.Admin_role;
import com.fire.service.system.AdminRoleService;
import com.fire.service.system.AdminService;
import com.fire.service.system.RoleService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Reference
    private AdminService adminService;

    @Reference
    private AdminRoleService adminRoleService;

    @GetMapping("/findAll")
    public List<Admin> findAll() {
        return adminService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Admin> findPage(int page, int size) {
        return adminService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Admin> findList(@RequestBody Map<String, Object> searchMap) {
        return adminService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Admin> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return adminService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public AdminRole findById(Integer id) {
        return adminRoleService.findById(id);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Object> map) {
        Admin admin = new Admin();
        String name = (String) map.get("loginName");
        String pass = (String) map.get("password");
        pass = new BCryptPasswordEncoder().encode(pass);
        admin.setStatus("1");
        admin.setPassword(pass);
        admin.setLoginName(name);
        Integer id = adminService.add(admin);
        System.out.println("************\n*********");
        System.out.println(id);
        List<Integer> list = (List) map.get("commit_role");

        Admin_role admin_role = new Admin_role();
        admin_role.setAdmin_id(id);
        for (Integer i : list) {
            admin_role.setRole_id(i);
            adminRoleService.add(admin_role);
            System.out.println(i);
        }

        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Map<String, Object> map) {
        Admin admin = new Admin();
        String name = (String) map.get("loginName");
        String pass = (String) map.get("password");
        Integer id = (Integer) map.get("id");
        admin.setLoginName(name);
        if (!"".equals(pass)) {
            pass = new BCryptPasswordEncoder().encode(pass);
        }
        admin.setPassword(pass);
        admin.setId(id);
        adminService.update(admin);

//        对admin和role联合表的修改
        List<Integer> list = (List) map.get("commit_role");
        adminRoleService.delById(id);
        Admin_role admin_role = new Admin_role();
        admin_role.setAdmin_id(id);
        for (Integer i : list) {
            admin_role.setRole_id(i);
            adminRoleService.add(admin_role);
            System.out.println(i);
        }
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id) {
        adminService.delete(id);
        return new Result();
    }

    @PostMapping("/updateAdminPassword")
    public Result updateAdminPassword(@RequestBody Map<String, String> map) {
        System.out.println(map.get("pass"));
        System.out.println(map.get("old"));
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> map1 = new HashMap();
        map1.put("loginName", map.get("loginName"));
        if (!name.equals(map.get("loginName"))) {
            throw new RuntimeException("登录名称有误");
        }
        List<Admin> list = adminService.findList(map1);
        if (list.size() == 0) {
            throw new RuntimeException("不存在该用户");
        }
        String password = list.get(0).getPassword();
        Integer id = list.get(0).getId();
        Admin admin = new Admin();
        if (BCrypt.checkpw(map.get("old"), password)) {
            System.out.println("success");

            String pass = new BCryptPasswordEncoder().encode(map.get("pass"));
            admin.setPassword(pass);
            admin.setId(id);
            adminService.update(admin);
        }
        return new Result();
    }
}
