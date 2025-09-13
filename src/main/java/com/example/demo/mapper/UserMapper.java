package com.example.demo.mapper;

import com.example.demo.dto.UserDomainStats;
import com.example.demo.dto.UserInfoResponse;
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
    
    /**
     * 分页查询用户列表
     * @param offset 偏移量
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 用户列表
     */
    @Select("SELECT * FROM user ORDER BY ${sortBy} ${sortDir} LIMIT #{offset}, #{size}")
    List<User> findUsersWithPagination(@Param("offset") Integer offset, 
                                      @Param("size") Integer size,
                                      @Param("sortBy") String sortBy,
                                      @Param("sortDir") String sortDir);
    
    /**
     * 获取用户总数
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM user")
    Long countUsers();
    
    /**
     * 根据关键词搜索用户（分页）
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 用户列表
     */
    @Select("SELECT * FROM user WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY ${sortBy} ${sortDir} LIMIT #{offset}, #{size}")
    List<User> searchUsersWithPagination(@Param("keyword") String keyword,
                                        @Param("offset") Integer offset, 
                                        @Param("size") Integer size,
                                        @Param("sortBy") String sortBy,
                                        @Param("sortDir") String sortDir);
    
    /**
     * 根据关键词搜索用户总数
     * @param keyword 搜索关键词
     * @return 搜索结果总数
     */
    @Select("SELECT COUNT(*) FROM user WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR email LIKE CONCAT('%', #{keyword}, '%')")
    Long countSearchUsers(@Param("keyword") String keyword);
    
    /**
     * 管理员查询用户信息（基础查询，不包含统计信息）
     */
    @Select("SELECT u.id, u.username, u.email, u.role, u.create_time, u.update_time, u.dom_num " +
            "FROM user u " +
            "WHERE (#{keyword} IS NULL OR #{keyword} = '' OR u.username LIKE CONCAT('%', #{keyword}, '%') OR u.email LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{userId} IS NULL OR u.id = #{userId}) " +
            "AND (#{role} IS NULL OR #{role} = '' OR u.role = #{role}) " +
            "ORDER BY u.create_time DESC " +
            "LIMIT #{offset}, #{size}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "email", column = "email"),
        @Result(property = "role", column = "role"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "domNum", column = "dom_num")
    })
    List<UserInfoResponse> selectUsersWithConditions(@Param("userId") Long userId,
                                                   @Param("keyword") String keyword,
                                                   @Param("role") String role,
                                                   @Param("createTimeStart") String createTimeStart,
                                                   @Param("createTimeEnd") String createTimeEnd,
                                                   @Param("offset") Integer offset,
                                                   @Param("size") Integer size,
                                                   @Param("sortBy") String sortBy,
                                                   @Param("sortDir") String sortDir);
    
    /**
     * 管理员查询用户信息总数
     */
    @Select("SELECT COUNT(*) FROM user u " +
            "WHERE (#{keyword} IS NULL OR #{keyword} = '' OR u.username LIKE CONCAT('%', #{keyword}, '%') OR u.email LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{userId} IS NULL OR u.id = #{userId}) " +
            "AND (#{role} IS NULL OR #{role} = '' OR u.role = #{role})")
    Long countUsersWithConditions(@Param("userId") Long userId,
                                 @Param("keyword") String keyword,
                                 @Param("role") String role,
                                 @Param("createTimeStart") String createTimeStart,
                                 @Param("createTimeEnd") String createTimeEnd);

    /**
     * 封禁用户
     * 将用户状态设置为BANNED，并记录封禁信息
     */
    @Update("UPDATE user SET status = 'BANNED', ban_reason = #{banReason}, ban_time = NOW(), ban_admin_id = #{banAdminId}, update_time = NOW() WHERE id = #{userId}")
    int banUser(@Param("userId") Long userId, 
                @Param("banReason") String banReason, 
                @Param("banAdminId") Long banAdminId);

    /**
     * 解封用户
     * 将用户状态设置为ACTIVE，并清除封禁信息
     */
    @Update("UPDATE user SET status = 'ACTIVE', ban_reason = NULL, ban_time = NULL, ban_admin_id = NULL, update_time = NOW() WHERE id = #{userId}")
    int unbanUser(@Param("userId") Long userId);

    /**
     * 根据用户ID查询用户状态
     * 用于检查用户是否被封禁
     */
    @Select("SELECT status, ban_reason, ban_time, ban_admin_id FROM user WHERE id = #{userId}")
    @Results({
        @Result(property = "status", column = "status"),
        @Result(property = "banReason", column = "ban_reason"),
        @Result(property = "banTime", column = "ban_time"),
        @Result(property = "banAdminId", column = "ban_admin_id")
    })
    User getUserBanStatus(@Param("userId") Long userId);

    /**
     * 检查用户是否被封禁
     * 返回true表示用户被封禁，false表示用户正常
     */
    @Select("SELECT COUNT(*) > 0 FROM user WHERE id = #{userId} AND status = 'BANNED'")
    boolean isUserBanned(@Param("userId") Long userId);
}
