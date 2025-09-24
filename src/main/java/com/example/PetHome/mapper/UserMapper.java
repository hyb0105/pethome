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
}