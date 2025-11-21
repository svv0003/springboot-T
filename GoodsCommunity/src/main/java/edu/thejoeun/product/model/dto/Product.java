package edu.thejoeun.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private int id;
    private String productName;
    private String productCode;
    private String category;
    private double price;
    private int stockQuantity;
    private String description;
    private String manufacturer;
    private String imageUrl;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

}
