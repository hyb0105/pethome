package com.example.PetHome.service;

import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.UserMapper;
import com.example.PetHome.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public boolean register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return false;
        }
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        user.setRole(0);
        return userMapper.insertUser(user) > 0;
    }

    public String login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null || !user.getPassword().equals(DigestUtils.md5Hex(password))) {
            return null;
        }
        return JwtUtils.createToken(user.getUsername(), user.getRole());
    }
}