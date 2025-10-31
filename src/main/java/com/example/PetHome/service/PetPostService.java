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

    // ... (createPost, auditPost, deletePost 保持不变) ...

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


    /**
     * 【【【 修复：修改这个方法的签名和逻辑 】】】
     * @param isAdmin 是否为管理员
     * @param principal 当前登录用户 (用于检查是否为作者)
     */
    public PetPost getPostDetail(Integer postId, boolean isAdmin, Principal principal) {
        PetPost post = petPostMapper.findPostDetailById(postId);
        if (post == null) {
            return null; // 帖子不存在
        }

        // 1. 如果是管理员，总可以查看
        if (isAdmin) {
            return post;
        }

        // 2. 如果帖子是“已审核”(status=1)，所有人都可以查看
        if (post.getStatus() == 1) {
            return post;
        }

        // 3. 如果帖子未审核 (status=0 or 2)，检查是否为作者本人
        if (principal != null) {
            User currentUser = userMapper.findByUsername(principal.getName());
            if (currentUser != null && post.getAuthorId() != null && post.getAuthorId().equals(currentUser.getId())) {
                return post; // 是作者本人，可以查看
            }
        }

        // 4. 其他所有情况 (未登录，或不是作者访问未审核帖子)
        return null;
    }

    // 获取帖子列表 (分页)
    public PageResult<PetPost> getAllPosts(String category, Integer status, String title, Integer pageNum, Integer pageSize, boolean isAdmin) {

        // 【关键权限】如果不是管理员，强制只能查看已审核(status=1)的帖子
        if (!isAdmin) {
            status = 1;
        }

        PageHelper.startPage(pageNum, pageSize);
        List<PetPost> posts = petPostMapper.findAllPosts(category, status, null, title);
        PageInfo<PetPost> pageInfo = new PageInfo<>(posts);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 获取当前登录用户的所有帖子 (分页)
     */
    public PageResult<PetPost> getMyPosts(String username, Integer status, Integer pageNum, Integer pageSize) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return new PageResult<>(0, List.of());
        }

        PageHelper.startPage(pageNum, pageSize);

        // 调用 findAllPosts，但这次传入 authorId
        List<PetPost> posts = petPostMapper.findAllPosts(null, status, user.getId(), null);

        PageInfo<PetPost> pageInfo = new PageInfo<>(posts);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}