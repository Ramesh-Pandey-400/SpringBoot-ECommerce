package com.application.firstapp.service;

import com.application.firstapp.exception.ResourceNotFoundException;
import com.application.firstapp.model.Cart;
import com.application.firstapp.model.Category;
import com.application.firstapp.model.Product;
import com.application.firstapp.payload.CartDTO;
import com.application.firstapp.payload.ProductDTO;
import com.application.firstapp.payload.ProductResponse;
import com.application.firstapp.repository.CartRepository;
import com.application.firstapp.repository.CategoryRepository;
import com.application.firstapp.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements  ProductService{
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Override
    public ProductResponse getProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%');
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductResponse getProductByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Category  category = categoryRepository.findById(categoryId).orElseThrow(()->new ResourceNotFoundException("category","categoryId",categoryId));
        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);

        List<ProductDTO> productDTOS =  products.stream().map(product -> modelMapper.map(product,ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;
    }

    @Override
    public ProductDTO addProduct(Long categoryid, ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryid).orElseThrow(()->new ResourceNotFoundException("category","categoryID", categoryid));

        Product product =  modelMapper.map(productDTO,Product.class);
        product.setCategory(category);
        double special_price =  product.getPrice() - (product.getPrice()*(product.getDiscount()*.01) );
        product.setImage("default.jpg");
        product.setSpecialPrice(special_price);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct,ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProduct(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);
        List<Product>  products = productPage.getContent();

        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();


        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return modelMapper.map(productResponse,ProductResponse.class);
    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {

        Product productFromDB = productRepository.findById(productId).
                orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        Product product = modelMapper.map(productDTO,Product.class);

        productFromDB.setProductName(product.getProductName());
        productFromDB.setDescription(product.getDescription());
        productFromDB.setQuantity(product.getQuantity());
        productFromDB.setDiscount(product.getDiscount());
        productFromDB.setPrice(product.getPrice());
        productFromDB.setSpecialPrice(product.getSpecialPrice());

        Product savedProduct = productRepository.save(productFromDB);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(), ProductDTO.class)).collect(Collectors.toList());

            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(), productId));

        return modelMapper.map(savedProduct,ProductDTO.class);


    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product productFromDB = productRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException("Product","ProductId",productId));

        // DELETE
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(), productId));

        productRepository.deleteById(productId);
        return modelMapper.map(productFromDB,ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImg(Long productId, MultipartFile image) throws IOException {
        //get the product from DB
        Product productFromDB = productRepository.findById(productId).
                orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        //upload image to server

        //get file name of uploaded img
        String path = "images/";
        String fileName =  uploadImage(path,image);
        //updating the new  File name to product
        productFromDB.setImage(fileName);
        //save product
        Product updatedProduct = productRepository.save(productFromDB);
        //return Product DTO after mapping
        return modelMapper.map(updatedProduct,ProductDTO.class);
    }

    private String uploadImage(String path, MultipartFile file) throws IOException {
        //file name of current file/original file
        String originalFileName = file.getOriginalFilename();
        //geerate unique file name
        String randomId = UUID.randomUUID().toString();
        //ramesh.png -> pandey -> pandey.png
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));
        String filePath = path + File.separator+fileName;

        //check if path exist and create
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        //upload to server
        Files.copy(file.getInputStream(), Paths.get(filePath));
        //rename the fileretrn file name
        return fileName;
    }

}
