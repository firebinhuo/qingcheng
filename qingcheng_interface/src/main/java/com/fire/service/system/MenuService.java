package com.fire.service.system;

import com.fire.entity.PageResult;
import com.fire.pojo.system.Menu;

import java.util.*;

/**
 * menu业务逻辑层
 */
public interface MenuService {


    public List<Menu> findAll();


    public PageResult<Menu> findPage(int page, int size);


    public List<Menu> findList(Map<String, Object> searchMap);


    public PageResult<Menu> findPage(Map<String, Object> searchMap, int page, int size);


    public Menu findById(String id);

    public void add(Menu menu);


    public void update(Menu menu);


    public void delete(String id);

    List<Map> findAllMenu();

    List<Map> findMenuByUsername(String username);

}
