package com.example.PetHome.service;

import com.example.PetHome.entity.PageResult;
import com.example.PetHome.entity.PetPost;
import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.PetPostMapper;
import com.example.PetHome.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.util.List;

@Service
public class PetPostService {

    @Autowired
    private PetPostMapper petPostMapper;

    @Autowired
    private UserMapper userMapper;

    // 用户创建帖子
    public PetPost createPost(PetPost post, String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        post.setAuthorId(user.getId());
        post.setStatus(0); // 0=待审核
        petPostMapper.insertPost(post);
        return post;
    }

    // (管理员) 审核帖子
    public boolean auditPost(Integer postId, Integer status, String rejectionReason) {
        PetPost post = petPostMapper.findPostById(postId);
        if (post == null) {
            return false;
        }
        // 拒绝时，理由不能为空 (在Controller层也应校验)
        if (status == 2 && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            rejectionReason = "未提供具体理由";
        }
        return petPostMapper.updatePostStatus(postId, status, status == 2 ? rejectionReason : null) > 0;
    }

    // (管理员/用户) 删除帖子
    public boolean deletePost(Integer postId, Principal principal) {
        User user = userMapper.findByUsername(principal.getName());
        PetPost post = petPostMapper.findPostById(postId);
        if (post == null || user == null) {
            return false;
        }
        // 只有管理员或帖子作者本人才能删除
        if (user.getRole() == 1 || post.getAuthorId().equals(user.getId())) {
            return petPostMapper.deletePostById(postId) > 0;
        }
        return false;
    }

    // 获取帖子详情
    public PetPost getPostDetail(Integer postId, boolean isAdmin) {
        PetPost post = petPostMapper.findPostDetailById(postId);
        if (post == null) {
            return null;
        }
        // 如果不是管理员，且帖子不是“已审核”状态，则不允许查看
        if (!isAdmin && post.getStatus() != 1) {
            return null;
        }
        return post;
    }

    // 获取帖子列表 (分页)
    public PageResult<PetPost> getAllPosts(String category, Integer status, Integer pageNum, Integer pageSize, boolean isAdmin) {

        // 【关键权限】如果不是管理员，强制只能查看已审核(status=1)的帖子
        if (!isAdmin) {
            status = 1;
        }

        PageHelper.startPage(pageNum, pageSize);
        // "status" 参数现在由权限逻辑控制
        List<PetPost> posts = petPostMapper.findAllPosts(category, status, null);
        PageInfo<PetPost> pageInfo = new PageInfo<>(posts);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}