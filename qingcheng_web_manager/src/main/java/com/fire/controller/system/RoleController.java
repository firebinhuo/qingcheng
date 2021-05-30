package com.fire.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.PageResult;
import com.fire.entity.Result;
import com.fire.pojo.system.Role;
import com.fire.pojo.system.Role_Resource;
import com.fire.service.system.RoleResourceService;
import com.fire.service.system.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Reference
    private RoleService roleService;

    @GetMapping("/findAll")
    public List<Role> findAll() {
        return roleService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Role> findPage(int page, int size) {
        return roleService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Role> findList(@RequestBody Map<String, Object> searchMap) {
        return roleService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Role> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return roleService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public Role findById(Integer id) {
        return roleService.findById(id);
    }

    @Reference
    private RoleResourceService roleResourceService;

    @PostMapping("/add")
    public Result add(@RequestBody Map<String, Object> map) {
        //联合主键
        Role_Resource role_resource = new Role_Resource();
        Integer role_id = (Integer) map.get("role_id");
        List<Integer> resourceList = (List) map.get("resources");
        System.out.println(role_id);
        for (Integer resource : resourceList) {
            System.out.println(resource);
        }
        /**
         * 添加角色权限对应表
         * 策略：先删除(根据role_id,删除对应的resource_id)，在再添加（role_id+res_id）
         */
        roleResourceService.deleteById(role_id);

        role_resource.setRole_id(role_id);

        for (Integer resource : resourceList) {
            role_resource.setResource_id(resource);
            roleResourceService.add(role_resource);
        }


        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Role role) {
        roleService.update(role);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id) {
        roleService.delete(id);
        return new Result();
    }

}
