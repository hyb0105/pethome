package com.example.PetHome.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String phone;
    private String email;
    private String avatar;
    private String realName;
    private String idCard;
    private Integer role;

    // 【新增字段】用于逻辑删除。0: 正常, -1: 已删除
    private Integer status;

    private Timestamp createTime;
    private Timestamp updateTime;
}