package com.fire.dao;

import com.fire.pojo.system.Menu;
import org.apache.ibatis.annotations.*;

import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface MenuMapper extends Mapper<Menu> {

    @Select("SELECT * FROM tb_menu WHERE id IN( " +
            "SELECT menu_id FROM tb_resource_menu WHERE resource_id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name='admin' " +
            ") " +
            ") " +
            ") " +
            ") " +
            "UNION " +
            "SELECT * FROM tb_menu WHERE id IN( " +
            "SELECT parent_id FROM tb_menu WHERE id IN( " +
            "SELECT menu_id FROM tb_resource_menu WHERE resource_id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name='admin' " +
            ") " +
            ") " +
            ") " +
            ") " +
            ") " +
            "UNION " +
            "SELECT * FROM tb_menu WHERE id IN ( " +
            "SELECT parent_id FROM tb_menu WHERE id IN( " +
            "SELECT parent_id FROM tb_menu WHERE id IN( " +
            "SELECT menu_id FROM tb_resource_menu WHERE resource_id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name='admin' " +
            ") " +
            ") " +
            ") " +
            ") " +
            ") " +
            ")")
    @Results(id = "resultMap", value = {
            @Result(property = "parentId", column = "parent_id")
    })
    public List<Menu> findMenuByUsername(@Param("login_name") String login_name);


}
