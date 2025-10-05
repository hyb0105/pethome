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

    // 【新增】获取用户个人资料
    public User getUserProfile(String username) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            // 出于安全考虑，返回前清空密码字段
            user.setPassword(null);
        }
        return user;
    }

    // 【新增】更新用户个人资料
    public User updateUserProfile(String username, User userUpdates) {
        User currentUser = userMapper.findByUsername(username);
        if (currentUser == null) {
            return null; // 用户不存在
        }

        // 将更新的字段设置到当前用户对象上
        currentUser.setPhone(userUpdates.getPhone());
        currentUser.setEmail(userUpdates.getEmail());
        currentUser.setAvatar(userUpdates.getAvatar());
        currentUser.setRealName(userUpdates.getRealName());
        currentUser.setIdCard(userUpdates.getIdCard());

        userMapper.updateUser(currentUser);

        // 返回更新后的用户信息（同样清空密码）
        return getUserProfile(username);
    }

    // 【新增】修改用户密码
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        User currentUser = userMapper.findByUsername(username);
        if (currentUser == null) {
            return false; // 用户不存在
        }

        // 1. 验证旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            return false; // 旧密码错误
        }

        // 2. 将新密码加密后更新到数据库
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(currentUser.getId(), encodedNewPassword);

        return true;
    }
}