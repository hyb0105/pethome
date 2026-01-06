package com.example.PetHome.mapper;

import com.example.PetHome.entity.PetPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PetPostMapper {
    int insertPost(PetPost post);

    // 【修改】所有查询都需要传入 currentUserId
    PetPost findPostById(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);
    PetPost findPostDetailById(@Param("id") Integer id, @Param("currentUserId") Integer currentUserId);
    List<PetPost> findAllPosts(@Param("category") String category,
                               @Param("status") Integer status,
                               @Param("authorId") Integer authorId,
                               @Param("title") String title,
                               @Param("currentUserId") Integer currentUserId); // 【新增】


    int updatePostStatus(@Param("id") Integer id,
                         @Param("status") Integer status,
                         @Param("rejectionReason") String rejectionReason);

    int deletePostById(Integer id);

    // 【【【 新增：更新计数的SQL 】】】
    int updateLikeCount(@Param("postId") Integer postId, @Param("amount") int amount);
    int incrementViewCount(Integer postId);
    Integer countPendingPosts();

}