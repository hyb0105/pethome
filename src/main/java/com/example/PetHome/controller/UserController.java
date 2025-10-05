package com.example.PetHome.controller;

import com.example.PetHome.entity.User;
import com.example.PetHome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity; // 新增导入
import java.security.Principal; // 新增导入
import com.example.PetHome.entity.PasswordChangeDTO; // 【新增】导入DTO
import java.util.Map; // 【新增】导入Map

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

    // 【新增】获取当前登录用户的个人资料
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()") // 确保用户已登录
    public ResponseEntity<User> getUserProfile(Principal principal) {
        User user = userService.getUserProfile(principal.getName());
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // 【新增】更新当前登录用户的个人资料
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()") // 确保用户已登录
    public ResponseEntity<User> updateUserProfile(@RequestBody User userUpdates, Principal principal) {
        User updatedUser = userService.updateUserProfile(principal.getName(), userUpdates);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    // 【新增】修改当前登录用户的密码
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()") // 确保用户已登录
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDTO passwordChangeDTO, Principal principal) {
        boolean success = userService.changePassword(
                principal.getName(),
                passwordChangeDTO.getOldPassword(),
                passwordChangeDTO.getNewPassword()
        );

        if (success) {
            return ResponseEntity.ok().body(Map.of("message", "密码修改成功"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "旧密码错误或操作失败"));
        }
    }
}