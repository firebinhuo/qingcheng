package com.fire.service.system;

import com.fire.pojo.system.Admin;
import com.fire.pojo.system.AdminRole;
import com.fire.pojo.system.Admin_role;

import java.util.List;

public interface AdminRoleService {
    void add(Admin_role adminRole);

    List<Admin_role> findAll();

    AdminRole findById(Integer id);

    void delById(Integer id);
}
