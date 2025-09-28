package com.example.PetHome.service;

import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.UserMapper;
import com.example.PetHome.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 导入 PasswordEncoder
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
// import org.apache.commons.codec.digest.DigestUtils; // 不再需要 MD5

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    // 注入我们在 SecurityConfig 中定义的那个 BCrypt 加密器
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired // 1. 在这里注入 JwtUtils
    private JwtUtils jwtUtils;
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public boolean register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false;
        }
        // 使用 BCrypt 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(0);
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return userMapper.insertUser(user) > 0;
    }

    public String login(String username, String password) {
        User user = userMapper.findByUsername(username);
        // 使用 BCrypt 的 matches 方法来比较明文密码和加密后的密码
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        return jwtUtils.createToken(user.getUsername(), user.getRole());
    }
}