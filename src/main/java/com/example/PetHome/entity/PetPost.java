package com.example.PetHome.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class PetPost {
    private Integer id;
    private String title;
    private String summary;
    private String content;
    private String category;
    private String coverImageUrl;
    private Integer authorId;
    private Integer status;
    private Integer views;
    private Integer likes;
    private Integer collections;
    private Timestamp createTime;
    private Timestamp updateTime;
    private Timestamp auditTime;
    private String rejectionReason;

    // DTO 辅助字段 (用于联表查询)
    private String authorName;
    // DTO 字段：当前登录的用户是否已点赞此帖
    private boolean likedByCurrentUser;
}