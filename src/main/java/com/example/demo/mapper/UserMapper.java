package com.example.demo.mapper;

import com.example.demo.dto.UserDomainStats;
import com.example.demo.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户Mapper接口
 * 使用MyBatis注解实现CRUD操作
 */
@Mapper
public interface UserMapper {
    
    /**
     * 查询所有用户
     */
    @Select("SELECT * FROM user")
    List<User> findAll();
    
    /**
     * 根据ID查询用户
     */
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);
    
    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);
    
    /**
     * 创建新用户
     */
    @Insert("INSERT INTO user(username, password, email, role) VALUES(#{username}, #{password}, #{email}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    /**
     * 保存用户（与insert方法相同，为了与Service层方法名一致）
     */
    @Insert("INSERT INTO user(username, password, email, role) VALUES(#{username}, #{password}, #{email}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int save(User user);
    
    /**
     * 更新用户信息
     */
    @Update("UPDATE user SET username = #{username}, password = #{password}, email = #{email}, role = #{role} WHERE id = #{id}")
    int update(User user);
    
    /**
     * 根据ID删除用户
     */
    @Delete("DELETE FROM user WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 获取用户可注册域名数量
     */
    @Select("SELECT dom_num FROM user WHERE id = #{userId}")
    Integer getUserDomainLimit(Long userId);
    
    /**
     * 更新用户可注册域名数量
     */
    @Update("UPDATE user SET dom_num = #{domNum} WHERE id = #{userId}")
    int updateUserDomainLimit(@Param("userId") Long userId, @Param("domNum") Integer domNum);
    
    /**
     * 获取用户域名使用统计
     */
    @Select("SELECT u.id, u.username, u.email, u.dom_num as available_domains, " +
            "IFNULL(active_count.cnt, 0) as used_domains, " +
            "CASE WHEN u.dom_num > 0 THEN 'CAN_REGISTER' ELSE 'LIMIT_REACHED' END as registration_status " +
            "FROM user u " +
            "LEFT JOIN (SELECT user_id, COUNT(*) as cnt FROM user_subdomain WHERE status = 'ACTIVE' GROUP BY user_id) active_count " +
            "ON u.id = active_count.user_id " +
            "WHERE u.id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "availableDomains", column = "available_domains"),
        @Result(property = "usedDomains", column = "used_domains"),
        @Result(property = "registrationStatus", column = "registration_status")
    })
    UserDomainStats getUserDomainStats(Long userId);
}
