package com.example.PetHome.mapper;

import com.example.PetHome.entity.PetPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PetPostMapper {
    int insertPost(PetPost post);

    PetPost findPostById(Integer id);

    // 联表查询作者名
    PetPost findPostDetailById(Integer id);

    List<PetPost> findAllPosts(@Param("category") String category,
                               @Param("status") Integer status,
                               @Param("authorId") Integer authorId,
                               @Param("title") String title);


    int updatePostStatus(@Param("id") Integer id,
                         @Param("status") Integer status,
                         @Param("rejectionReason") String rejectionReason);

    int deletePostById(Integer id);
}