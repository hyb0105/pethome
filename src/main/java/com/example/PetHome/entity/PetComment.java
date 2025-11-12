package com.example.PetHome.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class PetComment {
    private Integer id;
    private Integer petId;
    private Integer userId;
    private String content;
    private Timestamp createTime;

    // DTO 字段，用于联表查询显示用户名和头像
    private String authorName;
    private String authorAvatar;
    private String petName;
}