package com.example.PetHome.mapper;

import com.example.PetHome.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    // 根据用户名查询用户
    User findByUsername(@Param("username") String username);

    // 插入新用户
    int insertUser(User user);

    // 【新增】更新用户信息的方法
    int updateUser(User user);

    // 【新增】根据用户ID更新密码
    int updatePassword(@Param("id") Integer id, @Param("newPassword") String newPassword);
}