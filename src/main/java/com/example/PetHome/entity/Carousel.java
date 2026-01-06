package com.example.PetHome.entity;

import lombok.Data;

@Data
public class Carousel {
    private Integer id;
    private String imageUrl;
    private String linkUrl;
    private String title;
    private Integer sortOrder;
}