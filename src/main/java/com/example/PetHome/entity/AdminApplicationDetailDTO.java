package com.example.PetHome.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class AdminApplicationDetailDTO {
    // --- 申请信息 ---
    private Integer id;
    private Integer petId;
    private Integer adopterId;
    private String adopterName; // 申请时填写的姓名
    private String adopterPhone; // 申请时填写的电话
    private String reason;
    private Integer status;
    private Timestamp applicationTime;
    private Integer addressId;

    // --- 关联的宠物信息 ---
    private String petName;
    private String petType;
    private String petBreed;
    private String petPhotoUrl;
    // (可以按需添加更多宠物字段)

    // --- 关联的用户信息 (申请人) ---
    private String adopterUsername; // 用户的注册名
    private String adopterEmail;    // 用户的邮箱
    // (可以按需添加更多用户字段, 如 realName, idCard, 但要注意隐私)

    // --- 关联的地址信息 ---
    private String addressRecipientName; // 地址上的收件人
    private String addressPhone;         // 地址上的电话
    private String addressProvince;
    private String addressCity;
    private String addressDistrict;
    private String addressDetailedAddress;
    private Integer addressIsDefault;
    // 【【新增这一行】】
    private String rejectionReason;
}