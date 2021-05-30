package com.fire.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.PageResult;
import com.fire.entity.Result;
import com.fire.pojo.system.Resource;
import com.fire.pojo.system.Role_Resource;
import com.fire.service.system.ResourceService;
import com.fire.service.system.RoleResourceService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Reference
    private ResourceService resourceService;

    @Reference
    private RoleResourceService roleResourceService;

    @GetMapping("/findAll")
    public List<Map> findAll() {
        return resourceService.findAllResource();
    }

    @GetMapping("/findPage")
    public PageResult<Resource> findPage(int page, int size) {
        return resourceService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Integer> findList(@RequestBody Map<String, Object> searchMap) {
        List<Integer> list = new ArrayList();
        List<Role_Resource> role_resourceList = roleResourceService.findById(searchMap);
        for (Role_Resource role_resource : role_resourceList) {
            list.add(role_resource.getResource_id());
        }
        return list;

    }

    @PostMapping("/findPage")
    public PageResult<Resource> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return resourceService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public Resource findById(Integer id) {
        return resourceService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Resource resource) {
        resourceService.add(resource);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Resource resource) {
        resourceService.update(resource);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id) {
        resourceService.delete(id);
        return new Result();
    }


}
