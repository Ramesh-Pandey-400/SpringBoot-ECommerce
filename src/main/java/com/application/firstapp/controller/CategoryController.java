package com.application.firstapp.controller;

import com.application.firstapp.config.AppConstants;
import com.application.firstapp.model.Category;
import com.application.firstapp.payload.CategoryDTO;
import com.application.firstapp.payload.CategoryResponse;
import com.application.firstapp.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.service.annotation.DeleteExchange;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("api/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                             @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                             @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY,required = false) String sortBy,
                                                             @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_CATEGORY_ORDER ,required = false) String sortOrder){
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse,HttpStatus.ACCEPTED);
    }


    @PostMapping("api/admin/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid  @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.addCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO,HttpStatus.CREATED);
    }

    @DeleteMapping("api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
            CategoryDTO deletedCategory = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(deletedCategory, HttpStatus.OK);
    }

    @PutMapping("api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable Long categoryId){
            CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }

}
