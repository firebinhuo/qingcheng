package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.fire.dao.RoleResourceMapper;
import com.fire.pojo.system.Role;
import com.fire.pojo.system.Role_Resource;
import com.fire.service.system.RoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色权限组合关系的实现类
 */
@Service(interfaceClass = RoleResourceService.class)
public class RoleResourceImpl implements RoleResourceService {
    @Autowired
    private RoleResourceMapper roleResourceMapper;

    @Override
    public List<Role_Resource> findAll() {
        return roleResourceMapper.selectAll();
    }

    @Override
    public void add(Role_Resource role_resource) {
        roleResourceMapper.insertSelective(role_resource);
    }

    /**
     * 根据角色id删除对应所有的权限
     *
     * @param role_id
     */
    @Override
    public void deleteById(Integer role_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("role_id", role_id);
        Example example = createExample(map);
        roleResourceMapper.deleteByExample(example);
    }

    @Override
    public List<Role_Resource> findById(Map map) {
        Example example = createExample(map);
        return roleResourceMapper.selectByExample(example);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Role_Resource.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 角色id
            if (searchMap.get("role_id") != null) {
                criteria.andEqualTo("role_id", searchMap.get("role_id"));
            }

            // 权限id
            if (searchMap.get("resource_id") != null) {
                criteria.andEqualTo("resource_id", searchMap.get("resource_id"));
            }

        }
        return example;
    }
}
