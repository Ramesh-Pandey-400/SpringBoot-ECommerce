package com.application.firstapp.controller;

import com.application.firstapp.config.AppConstants;
import com.application.firstapp.model.Category;
import com.application.firstapp.model.Product;
import com.application.firstapp.payload.ProductDTO;
import com.application.firstapp.payload.ProductResponse;
import com.application.firstapp.repository.CategoryRepository;
import com.application.firstapp.repository.ProductRepository;
import com.application.firstapp.service.ProductService;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/")
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@PathVariable Long categoryId, @RequestBody ProductDTO productDTO){

        ProductDTO savedProductDTO = productService.addProduct(categoryId,productDTO);

        return new ResponseEntity<>(savedProductDTO,HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProduct(@RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                         @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                         @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY,required = false) String sortBy,
                                                         @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_CATEGORY_ORDER ,required = false) String sortOrder){
        ProductResponse productResponse = productService.getAllProduct(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(@PathVariable(name = "categoryId", required = false) Long categoryId,
                                                                @RequestParam(name= "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                                @RequestParam(name= "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY,required = false) String sortBy,
                                                                @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_CATEGORY_ORDER,required = false) String sortOrder){
        ProductResponse  productResponse = productService.getProductByCategory(categoryId,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @GetMapping("public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductByKeyword(@PathVariable(name="keyword",required = false) String keyword,
                                                               @RequestParam(name= "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                                               @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                                               @RequestParam(name= "sortBy", defaultValue = AppConstants.SORT_CATEGORY_BY,required = false) String sortBy,
                                                               @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_CATEGORY_ORDER,required = false) String sortOrder){
        ProductResponse productResponse = productService.getProductByKeyword(keyword,pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(productResponse,HttpStatus.OK);
    }

    @PutMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductDTO productDTO){
        ProductDTO  updateProductDTO = productService.updateProduct(productId,productDTO);
        return new ResponseEntity<>(updateProductDTO,HttpStatus.OK);
    }

    @DeleteMapping("admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId){
        ProductDTO productDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(productDTO,HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImg(@PathVariable Long productId,
                                                       @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO productDTO = productService.updateProductImg(productId,image);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
}
