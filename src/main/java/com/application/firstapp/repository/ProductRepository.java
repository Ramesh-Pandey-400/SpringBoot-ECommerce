package com.application.firstapp.repository;

import com.application.firstapp.model.Category;
import com.application.firstapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository   extends JpaRepository<Product,Long> {
    List<Product> findByCategoryOrderByPriceAsc(Category category);

    List<Product> findByProductNameLikeIgnoreCase(String s);
}
