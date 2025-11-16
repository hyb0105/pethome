package com.example.PetHome.controller;

import com.example.PetHome.entity.PetComment;
import com.example.PetHome.service.PetCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.PetHome.entity.PageResult;

import java.security.Principal;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/comments")
public class PetCommentController {

    @Autowired
    private PetCommentService commentService;

    // 获取某只宠物的所有评论
    @GetMapping("/pet/{petId}")
    @PreAuthorize("isAuthenticated()") // 只有登录用户才能看评论
    public ResponseEntity<List<PetComment>> getComments(@PathVariable Integer petId) {
        List<PetComment> comments = commentService.getCommentsByPetId(petId);
        return ResponseEntity.ok(comments);
    }

    // 发表新评论
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PetComment> postComment(@RequestBody PetComment comment, Principal principal) {
        PetComment createdComment = commentService.createComment(comment, principal.getName());
        if (createdComment != null) {
            return ResponseEntity.ok(createdComment);
        }
        return ResponseEntity.badRequest().build();
    }

    // 【【【 新增：管理员获取所有评论 】】】
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PageResult<PetComment> getAllComments(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String authorName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return commentService.getAllComments(content, authorName, pageNum, pageSize);
    }


    // 【【【 修改：删除评论的接口 】】】
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // 权限改为“已登录”
    public ResponseEntity<?> deleteComment(@PathVariable Integer id, Principal principal) { // 添加 Principal

        boolean success = commentService.deleteComment(id, principal.getName()); // 传入用户名

        if (success) {
            return ResponseEntity.ok(Map.of("message", "评论删除成功"));
        }
        // 403 Forbidden or 404 Not Found
        return ResponseEntity.status(403).body(Map.of("message", "无权删除或评论不存在"));
    }
}