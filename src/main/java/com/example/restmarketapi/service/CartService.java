package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.CartItemResponseDto;
import com.example.restmarketapi.dto.CartResponseDto;
import com.example.restmarketapi.entity.Cart;
import com.example.restmarketapi.entity.Product;
import com.example.restmarketapi.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final String PRODUCT_NOT_FOUND_MSG = "Product not found";
    private static final String PRODUCT_NOT_AVAILABLE_MSG = "The requested quantity of the product is not available";
    private static final String PRODUCT_UNAVAILABLE_IN_CART_MSG = "Product in cart is not found or unavailable";

    private final ProductRepository productRepository;
    private final Cart cart;

    public void addProductToCart(Long id, Integer quantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND_MSG));

        Map<Long, Integer> items = cart.getItems();

        int newQuantity = items.getOrDefault(id, 0) + quantity;

        if (product.getAvailable() < newQuantity) {
            throw new IllegalArgumentException(PRODUCT_NOT_AVAILABLE_MSG);
        }

        items.put(id, newQuantity);

    }

    public CartResponseDto getAll() {
        Map<Long, Integer> productsOnCart = cart.getItems();

        if (productsOnCart.isEmpty()) {
            return new CartResponseDto(new ArrayList<>(), BigDecimal.ZERO);
        }

        Set<Long> productIds = productsOnCart.keySet();

        List<Product> productsFromDb = productRepository.findAllById(productIds);

        Map<Long, Product> productMap = new HashMap<>();
        for (Product p : productsFromDb) {
            productMap.put(p.getId(), p);
        }

        List<CartItemResponseDto> listOfProductsInCart = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;
        int productNumberInCart = 1;

        for (Map.Entry<Long, Integer> entry : productsOnCart.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productMap.get(productId);
            if (product == null) {
                throw new IllegalArgumentException(PRODUCT_UNAVAILABLE_IN_CART_MSG);
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            listOfProductsInCart.add(new CartItemResponseDto(
                    productNumberInCart,
                    product.getTitle(),
                    quantity,
                    product.getPrice(),
                    subtotal
            ));

            productNumberInCart++;
            totalAmount = totalAmount.add(subtotal);
        }

        return new CartResponseDto(listOfProductsInCart, totalAmount);
    }

    public void removeProduct(Long id) {
        cart.getItems().remove(id);
    }

    public void changeNumberOfProduct(Long id, Integer newQuantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(PRODUCT_NOT_FOUND_MSG));
        if (product.getAvailable() < newQuantity) {
            throw new IllegalArgumentException(PRODUCT_NOT_AVAILABLE_MSG);
        }

        cart.getItems().put(id, newQuantity);
    }


}

