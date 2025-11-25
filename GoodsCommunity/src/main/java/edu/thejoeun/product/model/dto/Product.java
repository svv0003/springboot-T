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
    /*
    private Boolean isActive;
    - MySQL은 BOOLEAN 타입 사용 가능하지만
      ORACLE은 CHAR 이용하여 'Y', 'N' 형태로 주로 사용한다.

    만약 isActive 를 boolean 타입으로 제공한다면
    serviceImpl 에서
    isActive 가 'Y' 일 경우 boolean true 로 변환하여
    frontend 에 전달할 수 있으나,
    private String isActive 로 타입을 전달받아
    활용하는 것이 용이하다.
     */
    private String isActive;
    private String createdAt;
    private String updatedAt;

}
