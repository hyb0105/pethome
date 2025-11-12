package com.example.PetHome.service;

import com.example.PetHome.entity.PetComment;
import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.PetCommentMapper;
import com.example.PetHome.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.PetHome.entity.PageResult;
import com.github.pagehelper.PageHelper; // 【【新增】】
import com.github.pagehelper.PageInfo; // 【【新增】】

import java.util.List;
import java.security.Principal;

@Service
public class PetCommentService {

    @Autowired
    private PetCommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    // 根据宠物ID获取评论
    public List<PetComment> getCommentsByPetId(Integer petId) {
        return commentMapper.findCommentsByPetId(petId);
    }

    // 创建新评论
    public PetComment createComment(PetComment comment, String username) {
        User user = userMapper.findByUsername(username);
        if (user == null || comment.getPetId() == null || comment.getContent() == null) {
            return null;
        }

        comment.setUserId(user.getId());
        commentMapper.insertComment(comment);

        // 返回完整的评论（包括新生成的ID和时间戳）
        // 为了简单起见，我们只返回传入的对象，前端需要重新拉取列表
        return comment;
    }

    // 【【【 新增：管理员获取所有评论（分页）】】】
    public PageResult<PetComment> getAllComments(String content, String authorName, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PetComment> comments = commentMapper.findAllComments(content, authorName);
        PageInfo<PetComment> pageInfo = new PageInfo<>(comments);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    // 【【【 新增删除评论 】】】
    public boolean deleteComment(Integer commentId, String username) {
        User currentUser = userMapper.findByUsername(username);
        PetComment comment = commentMapper.findCommentById(commentId); // 使用我们刚添加的方法

        if (comment == null || currentUser == null) {
            return false; // 评论或用户不存在
        }

        // 核心权限检查：必须是管理员(role=1) 或是 评论的作者
        if (currentUser.getRole() == 1 || comment.getUserId().equals(currentUser.getId())) {
            return commentMapper.deleteCommentById(commentId) > 0;
        }

        return false; // 无权限
    }
}