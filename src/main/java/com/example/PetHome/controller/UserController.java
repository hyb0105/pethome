package com.example.PetHome.controller;

import com.example.PetHome.entity.User;
import com.example.PetHome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        if (userService.register(user)) {
            result.put("code", 200);
            result.put("message", "注册成功");
        } else {
            result.put("code", 500);
            result.put("message", "注册失败，用户名已存在");
        }
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginForm) {
        Map<String, Object> result = new HashMap<>();
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        String token = userService.login(username, password);
        if (token != null) {
            result.put("code", 200);
            result.put("message", "登录成功");
            result.put("token", token);
        } else {
            result.put("code", 500);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
}