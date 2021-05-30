package com.fire.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fire.entity.PageResult;
import com.fire.entity.Result;
import com.fire.pojo.system.Menu;
import com.fire.service.system.MenuService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/menu")
public class MenuController {

    @Reference
    private MenuService menuService;

    @GetMapping("/findAll")
    public List<Menu> findAll() {
        return menuService.findAll();
    }

    @GetMapping("/findMenu")
    public List<Map> findMenu() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Map> menuByUsername = menuService.findMenuByUsername(name);
        return menuByUsername;
    }

    @GetMapping("/findAllMenu")
    public List<Map> findAllMenu() {

        List<Map> menuByUsername = menuService.findAllMenu();
        return menuByUsername;
    }

    @GetMapping("/findPage")
    public PageResult<Menu> findPage(int page, int size) {
        return menuService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Menu> findList(@RequestBody Map<String, Object> searchMap) {
        return menuService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Menu> findPage(@RequestBody Map<String, Object> searchMap, int page, int size) {
        return menuService.findPage(searchMap, page, size);
    }

    @GetMapping("/findById")
    public Menu findById(String id) {
        return menuService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody Menu menu) {
        menuService.add(menu);
        return new Result();
    }

    @PostMapping("/update")
    public Result update(@RequestBody Menu menu) {
        menuService.update(menu);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(String id) {
        menuService.delete(id);
        return new Result();
    }


}
