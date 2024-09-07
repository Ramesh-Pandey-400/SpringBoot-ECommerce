package com.application.firstapp.repository;

import com.application.firstapp.model.Category;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(String categoryName);
}
