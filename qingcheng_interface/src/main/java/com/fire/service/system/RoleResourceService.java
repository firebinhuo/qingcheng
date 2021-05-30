package com.fire.service.system;

import com.fire.pojo.system.Resource;
import com.fire.pojo.system.Role_Resource;

import java.util.List;
import java.util.Map;

public interface RoleResourceService {

    List<Role_Resource> findAll();

    void add(Role_Resource role_resource);

    void deleteById(Integer role_id);

    List<Role_Resource> findById(Map map);
}
