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
import java.util.List;

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

    // 【新增】(管理员) 获取所有用户列表
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 【新增】(管理员) 更新指定ID用户的信息
    // 注意：这个 PUT 路由是 /api/user/{id}，
    // 而用户自己更新自己资料的路由是 /api/user/profile，
    // 两者不会冲突。
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> adminUpdateUser(@PathVariable Integer id, @RequestBody User userUpdates) {
        User updatedUser = userService.adminUpdateUserProfile(id, userUpdates);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    // 【【新增】】(管理员) 重置指定ID用户的密码
    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> adminResetPassword(@PathVariable Integer id, @RequestBody Map<String, String> passwordMap) {
        String newPassword = passwordMap.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "新密码不能为空"));
        }

        boolean success = userService.adminResetUserPassword(id, newPassword);

        if (success) {
            return ResponseEntity.ok().body(Map.of("message", "密码重置成功"));
        } else {
            return ResponseEntity.notFound().body(Map.of("message", "用户不存在"));
        }
    }

    // 【【新增】】(管理员) 删除指定ID的用户
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        // （可选）防止管理员删除自己
        // Principal principal
        // if (userService.findByUsername(principal.getName()).getId().equals(id)) {
        //     return ResponseEntity.badRequest().body(Map.of("message", "不能删除您自己的账户"));
        // }

        boolean success = userService.deleteUser(id);

        if (success) {
            return ResponseEntity.ok().body(Map.of("message", "用户删除成功"));
        } else {
            // 如果因为外键约束删除失败，这里也会返回 "false"
            return ResponseEntity.status(500).body(Map.of("message", "删除失败，可能该用户有关联数据（如领养申请）"));
        }
    }

}
