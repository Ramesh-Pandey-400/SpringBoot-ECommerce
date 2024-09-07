package com.application.firstapp.controller;

import com.application.firstapp.model.Cart;
import com.application.firstapp.payload.CartDTO;
import com.application.firstapp.repository.CartRepository;
import com.application.firstapp.service.CartService;
import com.application.firstapp.util.AuthUtil;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class CartController {

    @Autowired
    private CartService cartServices;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity){
        CartDTO  cartDTO = cartServices.addProductToCart(productId,quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.CREATED );
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTOs = cartServices.getAllCarts();
        return new ResponseEntity<>(cartDTOs,HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId = authUtil.loggedInEmail();
        Long cartId = cartRepository.findCartByEmail(emailId).getCartId();
        CartDTO cartDTO = cartServices.getCart(emailId,cartId);
        return new ResponseEntity<>(cartDTO,HttpStatus.FOUND);
    }

    @PutMapping("/cart/products/{productId}/qunatity/{operation}")
    public ResponseEntity<CartDTO> updateProductQuntity(@PathVariable Long productId,@PathVariable String operation){

        CartDTO cartDTO = cartServices.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete")? -1 : 1);
        return  new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,
                                                        @PathVariable Long productId){
        String status = cartServices.deleteProductFromCart(cartId,productId);
        return new ResponseEntity<>(status,HttpStatus.OK);
    }
}
