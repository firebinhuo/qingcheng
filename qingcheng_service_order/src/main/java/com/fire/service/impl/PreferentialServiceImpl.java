package com.fire.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.fire.dao.PreferentialMapper;
import com.fire.entity.PageResult;
import com.fire.pojo.order.Preferential;
import com.fire.service.order.PreferentialService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = PreferentialService.class)
public class PreferentialServiceImpl implements PreferentialService {

    @Autowired
    private PreferentialMapper preferentialMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Preferential> findAll() {
        return preferentialMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Preferential> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Preferential> preferentials = (Page<Preferential>) preferentialMapper.selectAll();
        return new PageResult<Preferential>(preferentials.getTotal(), preferentials.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Preferential> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return preferentialMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Preferential> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Preferential> preferentials = (Page<Preferential>) preferentialMapper.selectByExample(example);
        return new PageResult<Preferential>(preferentials.getTotal(), preferentials.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public Preferential findById(Integer id) {
        return preferentialMapper.selectByPrimaryKey(id);
    }

    /**
     * 新增
     *
     * @param preferential
     */
    public void add(Preferential preferential) {
        preferentialMapper.insert(preferential);
    }

    /**
     * 修改
     *
     * @param preferential
     */
    public void update(Preferential preferential) {
        preferentialMapper.updateByPrimaryKeySelective(preferential);
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Integer id) {
        preferentialMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int findPreMoneyByCategoryId(Integer categoryId, int money) {
//        指定查询条件在优惠规则计算表中查询
        /**
         * 指定的查询条件
         * 1.分类名称
         * 2.状态为 1
         * 3.消费额
         * 4.开始时间和截止时间
         * 5.排序（按消费额排序）
         */
        Example example = new Example(Preferential.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("state", "1");//状态
        criteria.andEqualTo("categoryId", categoryId);//分类
        criteria.andLessThanOrEqualTo("buyMoney", money);//消费额
        criteria.andGreaterThanOrEqualTo("endTime", new Date());//截止日期大于等于当前日期
        criteria.andLessThanOrEqualTo("startTime", new Date());//开始日期小于当前日期
        example.setOrderByClause("buy_money desc");//按购买金额降序查询

        List<Preferential> preferentials = preferentialMapper.selectByExample(example);
        if (preferentials.size() >= 1) {//有优惠
            Preferential preferential = preferentials.get(0);
            if ("1".equals(preferential.getType())){//不翻倍
                return preferential.getPreMoney();
            }else{//翻倍
//                计算倍数   money / buy_money
                int mulipie = money / preferential.getBuyMoney();
                return mulipie * preferential.getPreMoney();
            }
        } else {//没有优惠
            return 0;
        }
    }




    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Preferential.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 状态
            if (searchMap.get("state") != null && !"".equals(searchMap.get("state"))) {
                criteria.andLike("state", "%" + searchMap.get("state") + "%");
            }
            // 类型1不翻倍 2翻倍
            if (searchMap.get("type") != null && !"".equals(searchMap.get("type"))) {
                criteria.andLike("type", "%" + searchMap.get("type") + "%");
            }

            // ID
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 消费金额
            if (searchMap.get("buyMoney") != null) {
                criteria.andEqualTo("buyMoney", searchMap.get("buyMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 品类ID
            if (searchMap.get("categoryId") != null) {
                criteria.andEqualTo("categoryId", searchMap.get("categoryId"));
            }

        }
        return example;
    }

}
