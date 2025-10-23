package com.example.PetHome.entity;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class AdoptionApplication {
    private Integer id;
    private Integer petId;
    private Integer adopterId;
    private String adopterName;
    private String adopterPhone;
    private String reason;
    private Integer status;
    private Timestamp applicationTime;
    private Integer addressId;
}