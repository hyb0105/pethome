package com.example.PetHome.entity;

import lombok.Data;

@Data
public class PasswordChangeDTO {
    private String oldPassword;
    private String newPassword;
    // 【【新增这一行】】
    private String rejectionReason;
}