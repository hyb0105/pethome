package com.example.PetHome.service;

import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.UserMapper;
import com.example.PetHome.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 导入 PasswordEncoder
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.List;

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
    // 【新增】(管理员) 获取所有用户列表
    public List<User> getAllUsers() {
        List<User> users = userMapper.findAllUsers();
        // 遍历列表，清空密码
        for (User user : users) {
            user.setPassword(null);
        }
        return users;
    }

    // 【新增】(管理员) 更新指定ID用户的信息
    public User adminUpdateUserProfile(Integer userId, User userUpdates) {
        User userToUpdate = userMapper.findById(userId);
        if (userToUpdate == null) {
            return null; // 用户不存在
        }

        // 更新允许管理员修改的字段
        // 用户名(username) 和 密码(password) 不允许在此处修改
        userToUpdate.setPhone(userUpdates.getPhone());
        userToUpdate.setEmail(userUpdates.getEmail());
        userToUpdate.setAvatar(userUpdates.getAvatar());
        userToUpdate.setRealName(userUpdates.getRealName());
        userToUpdate.setIdCard(userUpdates.getIdCard());
        userToUpdate.setRole(userUpdates.getRole()); // 允许修改角色

        userMapper.adminUpdateUser(userToUpdate);

        // 返回更新后的用户信息
        userToUpdate.setPassword(null); // 清空密码
        return userToUpdate;
    }

    // 【新增】辅助方法，根据ID查找用户
    public User findById(Integer id) {
        return userMapper.findById(id);
    }
    // 【【新增】】(管理员) 重置用户密码
    public boolean adminResetUserPassword(Integer userId, String newPassword) {
        User userToUpdate = userMapper.findById(userId);
        if (userToUpdate == null) {
            return false; // 用户不存在
        }

        // 1. 将新密码加密
        String encodedNewPassword = passwordEncoder.encode(newPassword);

        // 2. 更新到数据库 (复用 updatePassword)
        userMapper.updatePassword(userId, encodedNewPassword);

        return true;
    }

    // 【【新增】】(管理员) 删除用户
    public boolean deleteUser(Integer userId) {
        // 检查用户是否存在
        if (userMapper.findById(userId) == null) {
            return false; // 用户不存在
        }

        // TODO: 在实际生产中，您可能需要先处理该用户关联的领养申请或宠物
        // (例如：将 adoption_application 表中对应的 adopter_id 设为 NULL)
        // 为保持简单，我们这里直接删除。如果存在外键约束，这里会失败。

        return userMapper.deleteUserById(userId) > 0;
    }
}