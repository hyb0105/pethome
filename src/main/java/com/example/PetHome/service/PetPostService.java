package com.example.PetHome.service;

import com.example.PetHome.entity.PageResult;
import com.example.PetHome.entity.PetPost;
import com.example.PetHome.entity.User;
import com.example.PetHome.mapper.PetPostMapper;
import com.example.PetHome.mapper.UserMapper;
// 【【新增导入】】
import com.example.PetHome.mapper.PostInteractionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.Principal;
import org.springframework.transaction.annotation.Transactional; // 【【新增】】

import java.time.LocalDate; // 【【新增】】
import java.util.List;

@Service
public class PetPostService {

    @Autowired
    private PetPostMapper petPostMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostInteractionMapper interactionMapper; // 【【新增】】

    // 辅助函数：通过 principal 获取用户
    private User getUserByPrincipal(Principal principal) {
        if (principal == null) return null;
        return userMapper.findByUsername(principal.getName());
    }
    // 辅助函数：获取用户ID
    private Integer getUserId(Principal principal) {
        User user = getUserByPrincipal(principal);
        return (user != null) ? user.getId() : null;
    }

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
    // 【【【 修复：添加 Principal, 并将 (postId) 改为 (postId, currentUserId) 】】】
    public boolean auditPost(Integer postId, Integer status, String rejectionReason, Principal principal) {
        Integer currentUserId = getUserId(principal); // <-- 定义 currentUserId
        PetPost post = petPostMapper.findPostById(postId, currentUserId); // <-- 修复标红错误
        if (post == null) {
            return false;
        }
        if (status == 2 && (rejectionReason == null || rejectionReason.trim().isEmpty())) {
            rejectionReason = "未提供具体理由";
        }
        return petPostMapper.updatePostStatus(postId, status, status == 2 ? rejectionReason : null) > 0;
    }

    // (管理员/用户) 删除帖子
    // 【【【 修复：并将 (postId) 改为 (postId, currentUserId) 】】】
    public boolean deletePost(Integer postId, Principal principal) {
        User user = getUserByPrincipal(principal);
        Integer currentUserId = (user != null) ? user.getId() : null; // <-- 定义 currentUserId

        PetPost post = petPostMapper.findPostById(postId, currentUserId); // <-- 修复标红错误
        if (post == null || user == null) {
            return false;
        }
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
    // 【修改】获取帖子详情
    public PetPost getPostDetail(Integer postId, boolean isAdmin, Principal principal) {
        Integer currentUserId = getUserId(principal);
        PetPost post = petPostMapper.findPostDetailById(postId, currentUserId);
        if (post == null) { return null; }
        if (isAdmin) { return post; }
        if (post.getStatus() == 1) { return post; }
        if (principal != null) {
            User currentUser = getUserByPrincipal(principal);
            if (currentUser != null && post.getAuthorId() != null && post.getAuthorId().equals(currentUser.getId())) {
                return post;
            }
        }
        return null;
    }

    // 【修改】获取帖子列表 (分页)
    public PageResult<PetPost> getAllPosts(String category, Integer status, String title, Integer pageNum, Integer pageSize, boolean isAdmin, Principal principal) {
        if (!isAdmin) { status = 1; }
        Integer currentUserId = getUserId(principal);

        PageHelper.startPage(pageNum, pageSize);
        List<PetPost> posts = petPostMapper.findAllPosts(category, status, null, title, currentUserId);
        PageInfo<PetPost> pageInfo = new PageInfo<>(posts);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 获取当前登录用户的所有帖子 (分页)
     */
    // 【修改】获取我的帖子
    public PageResult<PetPost> getMyPosts(String username, Integer status, Integer pageNum, Integer pageSize) {
        User user = userMapper.findByUsername(username);
        if (user == null) { return new PageResult<>(0, List.of()); }

        PageHelper.startPage(pageNum, pageSize);
        // 【修改】传入 user.getId() 作为 currentUserId 来检查点赞状态
        List<PetPost> posts = petPostMapper.findAllPosts(null, status, user.getId(), null, user.getId());
        PageInfo<PetPost> pageInfo = new PageInfo<>(posts);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    // 【【【 新增：点赞/取消点赞 】】】
    @Transactional
    public boolean toggleLike(Integer postId, String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) return false;

        if (interactionMapper.findLike(user.getId(), postId) > 0) {
            // 已点赞 -> 取消点赞
            interactionMapper.deleteLike(user.getId(), postId);
            petPostMapper.updateLikeCount(postId, -1);
        } else {
            // 未点赞 -> 点赞
            interactionMapper.insertLike(user.getId(), postId);
            petPostMapper.updateLikeCount(postId, 1);
        }
        return true;
    }

    // 【【【 新增：记录浏览 (一天一次) 】】】
    @Transactional
    public void recordView(Integer postId, String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) return; // 未登录不记录

        String lastViewed = interactionMapper.findLastViewDate(user.getId(), postId);
        String today = LocalDate.now().toString();

        if (lastViewed == null) {
            // 第一次看
            interactionMapper.insertView(user.getId(), postId);
            petPostMapper.incrementViewCount(postId);
        } else if (!lastViewed.equals(today)) {
            // 今天还没看
            interactionMapper.updateView(user.getId(), postId);
            petPostMapper.incrementViewCount(postId);
        }
        // else: 今天已经看过了，什么也不做
    }

    // 【【【 新增：获取我赞过的帖子 】】】
    public PageResult<PetPost> getLikedPosts(String username, Integer pageNum, Integer pageSize) {
        User user = userMapper.findByUsername(username);
        if (user == null) { return new PageResult<>(0, List.of()); }

        PageHelper.startPage(pageNum, pageSize);
        List<PetPost> posts = interactionMapper.findLikedPostsByUserId(user.getId(), user.getId());
        PageInfo<PetPost> pageInfo = new PageInfo<>(posts);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }
}