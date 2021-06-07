package com.fire.dao;

import com.fire.pojo.goods.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpecMapper extends Mapper<Spec> {
//    根据商品分类查询商品规格

    /**
     * @Description: 根据商品分类查询商品规格
     * @Author: fire
     * @Date: 2021/6/7 16:43
     */
    @Select("select name,options from tb_spec where template_id in ( " +
            "select template_id from tb_category where name='${name}') order by seq")
    public List<Map> findSpecByCategoryName(@Param("name") String categoryName);

}
