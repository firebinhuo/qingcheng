package com.fire.service.goods;

import com.fire.entity.PageResult;
import com.fire.pojo.goods.Spec;

import java.util.*;

/**
 * spec业务逻辑层
 */
public interface SpecService {


    public List<Spec> findAll();


    public PageResult<Spec> findPage(int page, int size);


    public List<Spec> findList(Map<String, Object> searchMap);


    public PageResult<Spec> findPage(Map<String, Object> searchMap, int page, int size);


    public Spec findById(Integer id);

    public void add(Spec spec);


    public void update(Spec spec);


    public void delete(Integer id);

}
