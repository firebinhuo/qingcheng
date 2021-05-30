package com.fire.dao;

import com.fire.pojo.order.CategoryReport;
import com.fire.pojo.system.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.time.LocalDate;
import java.util.List;

public interface ResourceMapper extends Mapper<Resource> {

    public List<CategoryReport> categoryReport(@Param("date") LocalDate date);

    @Select("SELECT res_key FROM tb_resource WHERE id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name='${login_name}' " +
            ") " +
            ") " +
            ")")
    List<String> findResKeyByLoginName(@Param("login_name") String login_name);

}
