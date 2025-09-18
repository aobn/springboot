package com.example.demo.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.entity.Admin;

/**
 * 文件名：AdminMapper.java
 * 功能：管理员数据访问接口，提供对admin表的CRUD操作
 * 作者：CodeBuddy
 * 创建时间：2025-08-11
 * 版本：v1.0.0
 */
@Mapper
public interface AdminMapper {
    
    /**
     * 根据ID查询管理员
     * @param id 管理员ID
     * @return 管理员对象，如果不存在则返回null
     */
    @Select("SELECT * FROM admin WHERE id = #{id}")
    Admin findById(Long id);
    
    /**
     * 根据用户名查询管理员
     * @param username 用户名
     * @return 管理员对象，如果不存在则返回null
     */
    @Select("SELECT * FROM admin WHERE username = #{username}")
    Admin findByUsername(String username);
    
    /**
     * 根据邮箱查询管理员
     * @param email 邮箱
     * @return 管理员对象，如果不存在则返回null
     */
    @Select("SELECT * FROM admin WHERE email = #{email}")
    Admin findByEmail(String email);
    
    /**
     * 插入新管理员
     * @param admin 管理员对象
     * @return 影响的行数
     */
    @Insert("INSERT INTO admin(username, password, email, role) VALUES(#{username}, #{password}, #{email}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);
    
    /**
     * 更新管理员信息
     * @param admin 管理员对象
     * @return 影响的行数
     */
    @Update("UPDATE admin SET username = #{username}, password = #{password}, email = #{email}, role = #{role} WHERE id = #{id}")
    int update(Admin admin);
}