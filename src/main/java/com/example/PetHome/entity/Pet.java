package com.example.PetHome.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Pet {
    private Integer id;
    private String name;
    private String type;
    private String breed;
    private Integer gender;
    private Integer age;
    private String description;
    private String healthStatus;
    private Integer sterilization;
    private String vaccination;
    private String photoUrl;
    private Integer status;
    private Integer ownerId;
    private Timestamp createTime;
}