package com.application.firstapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long productId;

    private String productName;
    private String description;
    private Integer quantity;
    private double price;
    private double discount;
    private double specialPrice;
    private String image;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name  = "seller_id")
    private User user;

    @OneToMany(mappedBy = "product",cascade = {CascadeType.PERSIST,CascadeType.MERGE},fetch = FetchType.EAGER)
    private List<CartItem> products = new ArrayList<>();

}
