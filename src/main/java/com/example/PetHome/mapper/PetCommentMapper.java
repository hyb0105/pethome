package com.example.PetHome.mapper;

import com.example.PetHome.entity.PetComment;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PetCommentMapper {
    // 插入新评论
    int insertComment(PetComment comment);

    // 根据宠物ID查询所有评论（包含用户名）
    List<PetComment> findCommentsByPetId(Integer petId);
    /**
     * (管理员) 搜索所有评论
     * @param content 评论内容 (模糊搜索)
     * @param authorName 作者名 (模糊搜索)
     */
    List<PetComment> findAllComments(@Param("content") String content, @Param("authorName") String authorName);
    // 【【【 新增：根据ID查找评论 】】】
    PetComment findCommentById(Integer id);

    /**
     * (管理员) 删除评论
     */
    int deleteCommentById(Integer id);

    // 【【【 新增这个方法 】】】
    int deleteCommentsByPetId(Integer petId);
}