package com.example.PetHome.mapper;

import com.example.PetHome.entity.PetPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PostInteractionMapper {

    // --- Likes ---
    int findLike(@Param("userId") Integer userId, @Param("postId") Integer postId);
    int insertLike(@Param("userId") Integer userId, @Param("postId") Integer postId);
    int deleteLike(@Param("userId") Integer userId, @Param("postId") Integer postId);

    // 用于“我赞过的”列表 (需要联表查询)
    List<PetPost> findLikedPostsByUserId(@Param("userId") Integer userId, @Param("currentUserId") Integer currentUserId);

    // --- Views ---
    String findLastViewDate(@Param("userId") Integer userId, @Param("postId") Integer postId);
    int insertView(@Param("userId") Integer userId, @Param("postId") Integer postId);
    int updateView(@Param("userId") Integer userId, @Param("postId") Integer postId);
}