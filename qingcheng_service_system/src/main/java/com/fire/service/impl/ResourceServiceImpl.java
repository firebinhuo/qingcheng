package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.fire.dao.ResourceMapper;
import com.fire.entity.PageResult;
import com.fire.pojo.system.Resource;
import com.fire.service.system.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Resource> findAll() {
        return resourceMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Resource> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Resource> resources = (Page<Resource>) resourceMapper.selectAll();
        return new PageResult<Resource>(resources.getTotal(), resources.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Resource> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return resourceMapper.selectByExample(example);
    }

    /**
     * 树状查询resource表（id组合parent_id）
     */

    public List<Map> findAllResource() {
        List<Resource> resourceList = findAll();
        return findResourceByParentId(resourceList, 0);

    }

    public List<Map> findResourceByParentId(List<Resource> resourceList, Integer parent_id) {
        List<Map> mapList = new ArrayList<>();
        for (Resource resource : resourceList) {
            Map map = new HashMap();
            if (resource.getParentId().equals(parent_id)) {
                map.put("resource", resource);
                map.put("children", findResourceByParentId(resourceList, resource.getId()));
                mapList.add(map);
            }
        }
        return mapList;
    }

    /**
     * 根据用户名查找对应的权限
     *
     * @param s
     * @return
     */
    @Override
    public List<String> findResKeyByLoginName(String s) {
        return resourceMapper.findResKeyByLoginName(s);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Resource> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Resource> resources = (Page<Resource>) resourceMapper.selectByExample(example);
        return new PageResult<Resource>(resources.getTotal(), resources.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Resource findById(Integer id) {
        return resourceMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param resource
     */
    public void add(Resource resource) {
        resourceMapper.insert(resource);
    }

    /**
     * 修改
     *
     * @param resource
     */
    public void update(Resource resource) {
        resourceMapper.updateByPrimaryKeySelective(resource);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Integer id) {
        resourceMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Resource.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // res_key
            if (searchMap.get("resKey") != null && !"".equals(searchMap.get("resKey"))) {
                criteria.andLike("resKey", "%" + searchMap.get("resKey") + "%");
            }
            // res_name
            if (searchMap.get("resName") != null && !"".equals(searchMap.get("resName"))) {
                criteria.andLike("resName", "%" + searchMap.get("resName") + "%");
            }

            // id
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // parent_id
            if (searchMap.get("parentId") != null) {
                criteria.andEqualTo("parentId", searchMap.get("parentId"));
            }

        }
        return example;
    }

}
