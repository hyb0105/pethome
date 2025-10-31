package com.example.PetHome.controller;

import com.example.PetHome.entity.PageResult;
import com.example.PetHome.entity.PetPost;
import com.example.PetHome.service.PetPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import lombok.Data;

@RestController
@RequestMapping("/api/posts")
public class PetPostController {

    @Autowired
    private PetPostService petPostService;

    // 检查当前用户是否为管理员
    private boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    // 用户发布新帖子
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PetPost> createPost(@RequestBody PetPost post, Principal principal) {
        PetPost createdPost = petPostService.createPost(post, principal.getName());
        if (createdPost != null) {
            return ResponseEntity.ok(createdPost);
        }
        return ResponseEntity.badRequest().build();
    }

    // (公开/管理员) 获取帖子列表
    @GetMapping
    public PageResult<PetPost> getAllPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status, // 管理员可按状态筛选
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = isAdmin(auth);

        return petPostService.getAllPosts(category, status, pageNum, pageSize, isAdmin);
    }

    // (公开/管理员) 获取帖子详情
    @GetMapping("/{id}")
    public ResponseEntity<PetPost> getPostDetail(@PathVariable Integer id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = isAdmin(auth);

        PetPost post = petPostService.getPostDetail(id, isAdmin);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // (管理员) 审核帖子 (批准或拒绝)
    @PutMapping("/{id}/audit")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> auditPost(@PathVariable Integer id, @RequestBody AuditRequest request) {
        if (request.getStatus() == null || (request.getStatus() != 1 && request.getStatus() != 2)) {
            return ResponseEntity.badRequest().body(Map.of("message", "无效的状态值"));
        }
        if (request.getStatus() == 2 && (request.getRejectionReason() == null || request.getRejectionReason().trim().isEmpty())) {
            return ResponseEntity.badRequest().body(Map.of("message", "拒绝时必须填写理由"));
        }

        boolean success = petPostService.auditPost(id, request.getStatus(), request.getRejectionReason());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "审核操作成功"));
        }
        return ResponseEntity.notFound().build();
    }

    // (管理员/作者) 删除帖子
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable Integer id, Principal principal) {
        boolean success = petPostService.deletePost(id, principal);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "帖子删除成功"));
        }
        // 403 Forbidden or 404 Not Found
        return ResponseEntity.status(403).body(Map.of("message", "无权删除或帖子不存在"));
    }

    @Data
    static class AuditRequest {
        private Integer status;
        private String rejectionReason;
    }
}