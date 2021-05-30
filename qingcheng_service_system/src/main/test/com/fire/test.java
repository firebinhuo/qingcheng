package com.fire;

import com.fire.dao.MenuMapper;
import com.fire.pojo.system.Admin;
import com.fire.pojo.system.Admin_role;
import com.fire.pojo.system.Menu;
import com.fire.pojo.system.Resource;
import com.fire.service.system.AdminRoleService;
import com.fire.service.system.AdminService;
import com.fire.service.system.MenuService;
import com.fire.service.system.ResourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = "classpath*:application*.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class test {
    //    @Autowired
//    private AdminRoleService adminRoleService;
    //
//    @Autowired
//    private AdminService adminService;
//    @Autowired
//    private ResourceService resourceService;
//    @Autowired
//    private MenuService menuService;
//    @Autowired
//    private MenuMapper menuMapper;

//    @Test
//    public void testAdminRole() {
//        List<Admin_role> all = adminRoleService.findAll();
//        for (Admin_role admin_role : all) {
//            System.out.println(admin_role.getRole_id());
//            System.out.println(admin_role.getAdmin_id());
//
//        }
//    }
//
//    @Test
//    public void testAdminRoleAdd() {
//        Admin_role admin_role = new Admin_role();
//        admin_role.setAdmin_id(3);
//        admin_role.setRole_id(1);
//        adminRoleService.add(admin_role);
//    }
//
//    @Test
//    public void testAdminInsert() {
//        Admin admin = new Admin();
//        admin.setLoginName("fire");
//        admin.setPassword("11111");
//        admin.setStatus("1");
//
//        adminService.add(admin);
//        System.out.println("************\n*********");
//        System.out.println(admin.getId());
//    }
//
//    @Test
//    public void testRoleResource() {
//        List<Map> allResource = resourceService.findAllResource();
//        for (Map map : allResource) {
//            Resource resource = (Resource) map.get("resource");
//            System.out.println(resource.getParentId());
//            System.out.println("****************");
//            List<Map> children = (List<Map>) map.get("children");
//            for (Map child : children) {
//                Resource resource1 = (Resource) child.get("resource");
////                System.out.println(resource1.getResKey());
//                System.out.println(resource1.getResName());
//                System.out.println(resource1.getParentId());
//            }
//            System.out.println();
//        }
//    }
//
//    @Test
//    public void test() {
//        List<String> admin = resourceService.findResKeyByLoginName("admin");
//        System.out.println(admin);
//    }
//
//    @Test
//    public void testFindMenuByUsername() {
//        List<Map> menu = menuService.findMenuByUsername("admin");
//
//    }
//
//    @Test
//    public void test1() {
//        List<Menu> menuList = menuMapper.selectAll();
//        for (Menu menu : menuList) {
//            System.out.print(menu.getId());
//            System.out.print(menu.getParentId());
//            System.out.print(menu.getName());
//            System.out.print(menu.getUrl());
//            System.out.println("  ");
//        }
//    }
}
