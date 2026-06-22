package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.CartItemResponseDto;
import com.example.restmarketapi.dto.CartResponseDto;
import com.example.restmarketapi.entity.Cart;
import com.example.restmarketapi.entity.Product;
import com.example.restmarketapi.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {
    private final ProductRepository productRepository;
    private final Cart cart;

    public CartService(ProductRepository productRepository, Cart cart) {
        this.productRepository = productRepository;
        this.cart = cart;
    }

    public void addProductToCart(Long id, Integer quantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        Map<Long, Integer> items = cart.getItems();

        int newQuantity = items.getOrDefault(id, 0) + quantity;

        if (product.getAvailable() < newQuantity) {
            throw new IllegalArgumentException("Only " + product.getAvailable() + " units of the product are available");
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
                throw new IllegalArgumentException("Product with ID " + productId + " not found or unavailable");
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
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getAvailable() < newQuantity) {
            throw new IllegalArgumentException("Only " + product.getAvailable() + " units of the product are available");
        }

        cart.getItems().put(id, newQuantity);
    }


}

