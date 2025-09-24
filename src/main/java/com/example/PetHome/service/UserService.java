package com.example.PetHome.service;

import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.UserMapper;
import com.example.PetHome.utils.JwtUtils; // 引入 JWT 工具类
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils; // 引入 MD5 工具

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false;
        }
        // 密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRole(0); // 默认普通用户
        return userMapper.insertUser(user) > 0;
    }

    public String login(String username, String password) {
        User user = userMapper.findByUsername(username);
        // 检查用户是否存在以及密码是否匹配
        if (user == null || !user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))) {
            return null; // 登录失败
        }
        // 登录成功，生成并返回JWT Token
        return JwtUtils.createToken(user.getUsername(), user.getRole());
    }
}