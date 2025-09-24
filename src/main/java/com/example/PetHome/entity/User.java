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
    private Timestamp createTime;
    private Timestamp updateTime;
}