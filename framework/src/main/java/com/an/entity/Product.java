package com.an.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Product {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Long categoryId;
    private String imageUrl;
    private Integer isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
