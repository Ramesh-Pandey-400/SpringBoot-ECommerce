package com.application.firstapp.service;

import com.application.firstapp.payload.ProductDTO;
import com.application.firstapp.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductResponse getProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductByCategory(Long categoryId,
                                         Integer pageNumber,
                                         Integer pageSize,
                                         String sortBy,
                                         String sortOrder);

    ProductDTO addProduct(Long categoryId, ProductDTO product);
    ProductResponse getAllProduct(Integer pageNumber,
                                  Integer pageSize,
                                  String sortBy,
                                  String sortOrder);

    ProductDTO updateProduct(Long productId, ProductDTO product);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImg(Long productId, MultipartFile image) throws IOException;
}
