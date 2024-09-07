package com.application.firstapp.service;

import com.application.firstapp.exception.APIException;
import com.application.firstapp.exception.ResourceNotFoundException;
import com.application.firstapp.model.Category;
import com.application.firstapp.payload.CategoryDTO;
import com.application.firstapp.payload.CategoryResponse;
import com.application.firstapp.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImp implements CategoryService {


    private int next = 1;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy, String sortOrder) {
        List<Category> allcategories = categoryRepository.findAll();

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
         if(categories.isEmpty())
            throw  new APIException("No Category Created till now.");

        List<CategoryDTO>  categoryDTOS =categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class)).toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        return categoryResponse;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {

        Category category = modelMapper.map(categoryDTO,Category.class);

        Category categoryFromDB =  categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDB!=null)
            throw new APIException("Category with Catrgory name"+ category.getCategoryName()+"Already Exist");
        category.setCategoryId(next++);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",categoryId));
        categoryRepository.deleteById(categoryId);
        return modelMapper.map(category,CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {

        Category categoryFromDB= categoryRepository.findById(categoryId)
                .orElseThrow(()-> new ResourceNotFoundException("Category","CategoryId",categoryId));
        Category category =  modelMapper.map(categoryDTO,Category.class);
        category.setCategoryId(categoryId);
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,CategoryDTO.class);
    }

}
