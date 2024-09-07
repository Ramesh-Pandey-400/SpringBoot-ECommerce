package com.application.firstapp.service;

import com.application.firstapp.model.Category;
import com.application.firstapp.payload.CategoryDTO;
import com.application.firstapp.payload.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder);
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(CategoryDTO categoryDTO,Long categoryId);
}
