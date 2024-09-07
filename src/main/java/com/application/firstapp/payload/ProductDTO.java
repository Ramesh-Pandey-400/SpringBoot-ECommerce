package com.application.firstapp.payload;

import com.application.firstapp.model.Category;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private long productID;
    private String description;
    private String productName;
    private String image;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;
    private Category category;
}
