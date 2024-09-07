package com.application.firstapp.service;

import com.application.firstapp.exception.APIException;
import com.application.firstapp.exception.ResourceNotFoundException;
import com.application.firstapp.model.Cart;
import com.application.firstapp.model.CartItem;
import com.application.firstapp.model.Product;
import com.application.firstapp.payload.CartDTO;
import com.application.firstapp.payload.CartItemDTO;
import com.application.firstapp.payload.ProductDTO;
import com.application.firstapp.repository.CartItemRepository;
import com.application.firstapp.repository.CartRepository;
import com.application.firstapp.repository.ProductRepository;
import com.application.firstapp.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService{


    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        //Find existing cart or create one
        Cart cart = createCart();
        //Retrieve product details
        Product product= productRepository.findById(productId).orElseThrow(()->new ResourceNotFoundException("product","productId",productId));
        //fetching cart item
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
        //Perform Validation
        if(cartItem!=null){
            throw new APIException("Product "+product.getProductName() + " already exist in cart");
        }
        if(product.getQuantity()==0){
            throw new APIException(product.getProductName() +" is not available");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please make an order of the "+product.getProductName() +
                    "less than or equal to quntity "+product.getQuantity()+".");
        }
        //Create Cart Item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        //Save cart item
        cartItemRepository.save(newCartItem);
        //return updated cart
        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
        cartRepository.save(cart);


        //model mapper to convert
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(item-> {
            ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
            map.setQuantity(item.getQuantity());
            return map;
        });
        cartDTO.setProducts(productStream.toList());
        return cartDTO;

    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.size() == 0) {
            throw new APIException("No cart exists");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());


            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,cartId);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        if(cart==null){
            throw new ResourceNotFoundException("cart","cartId",cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<ProductDTO> productDTOs = cart.getCartItems().stream()
                .map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                .toList();
        cartDTO.setProducts(productDTOs);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String email =  authUtil.loggedInEmail();
        Cart userCart =  cartRepository.findCartByEmail(email);
        Long cartId =  userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("cart","cartId",cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

        if(product.getQuantity()==0){
            throw new APIException(product.getProductName() + "is not available!");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please make an order of "+product.getProductName()
            +" less than or equal to quantity"+product.getQuantity());
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("product "+product.getProductName() + "not found");
        }

        int newQuantity = cartItem.getQuantity()+quantity;
        if(newQuantity<0){
            throw new APIException("Quantity cannot be negative");
        }
        if(newQuantity==0){
            deleteProductFromCart(cartId,productId);
        }
        else{
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity()+quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice()+ (cartItem.getProductPrice()*quantity));
        cartRepository.save(cart);
        }
        CartItem updatedItem = cartItemRepository.save(cartItem);

        if(cartItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item->{
                    ProductDTO prd = modelMapper.map(item.getProduct(),ProductDTO.class);
                    prd.setQuantity(item.getQuantity());
                    return prd;
                });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).
                orElseThrow(()-> new ResourceNotFoundException("Cart","cartID",cartId));
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItem==null){
            throw  new ResourceNotFoundException("product","productId",productId);
        }

        cart.setTotalPrice(cartItem.getProductPrice() -
                (cartItem.getProductPrice()*cartItem.getQuantity()));


        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);
        return "Product"+cartItem.getProduct().getProductName()+" removed from cart";
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }



    private Cart createCart() {
        Cart userCart= cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null){
            return  userCart;
        }
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart =  cartRepository.save(cart);
        return  newCart;
    }
}
