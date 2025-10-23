package com.example.PetHome.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Address {
    private Integer id;
    private Integer userId;
    private String recipientName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detailedAddress;
    private Integer isDefault; // 使用 Integer 对应 TINYINT(1)
    private Timestamp createTime;
    private Timestamp updateTime;
}