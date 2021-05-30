package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fire.dao.AdminMapper;
import com.fire.dao.AdminRoleMapper;
import com.fire.dao.RoleMapper;
import com.fire.pojo.system.Admin;
import com.fire.pojo.system.AdminRole;
import com.fire.pojo.system.Admin_role;
import com.fire.pojo.system.Role;
import com.fire.service.system.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service(interfaceClass = AdminRoleService.class)
public class AdminRoleServiceImpl implements AdminRoleService {
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Transactional
    @Override
    public void add(Admin_role adminRole) {
        adminRoleMapper.insert(adminRole);
    }

    @Override
    public List<Admin_role> findAll() {
        return adminRoleMapper.selectAll();
    }

    @Override
    public AdminRole findById(Integer id) {
        AdminRole adminRole = new AdminRole();
        //查询admin
        Admin admin = adminMapper.selectByPrimaryKey(id);
        adminRole.setAdmin(admin);
//        查询role
        List<Role> roleList = new ArrayList<>();
        Example example = new Example(Admin_role.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("admin_id", id);
        List<Admin_role> admin_roleList = adminRoleMapper.selectByExample(example);
        for (Admin_role admin_role : admin_roleList) {
            Role role1 = roleMapper.selectByPrimaryKey(admin_role.getRole_id());
            roleList.add(role1);
        }
        adminRole.setRole(roleList);
        return adminRole;
    }


    @Override
    public void delById(Integer id) {
        Example example = new Example(Admin_role.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("admin_id", id);
        adminRoleMapper.deleteByExample(example);
    }
}
