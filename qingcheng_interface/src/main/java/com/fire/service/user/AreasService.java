package com.fire.service.user;

import com.fire.entity.PageResult;
import com.fire.pojo.user.Address;
import com.fire.pojo.user.Areas;

import java.util.*;

/**
 * areas业务逻辑层
 */
public interface AreasService {


    public List<Areas> findAll();


    public PageResult<Areas> findPage(int page, int size);


    public List<Areas> findList(Map<String, Object> searchMap);


    public PageResult<Areas> findPage(Map<String, Object> searchMap, int page, int size);


    public Areas findById(String areaid);

    public void add(Areas areas);


    public void update(Areas areas);


    public void delete(String areaid);

}
