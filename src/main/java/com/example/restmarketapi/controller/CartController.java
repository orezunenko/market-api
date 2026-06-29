package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.CartItemRequestDto;
import com.example.restmarketapi.dto.CartResponseDto;
import com.example.restmarketapi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Manage products in the shopping cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get cart contents", description = "Returns all items in the cart and the total price.")
    public CartResponseDto getAll() {
        return cartService.getAll();
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product or increases its quantity if already in the cart.")
    public void addProductToCart(@RequestBody CartItemRequestDto requestDto) {
        cartService.addProductToCart(requestDto.getId(), requestDto.getQuantity());
    }

    @PutMapping("/items")
    @Operation(summary = "Update item quantity", description = "Sets a new exact quantity for a specific cart item.")
    public void changeNumberOfProduct(@RequestBody CartItemRequestDto requestDto) {
        cartService.changeNumberOfProduct(requestDto.getId(), requestDto.getQuantity());
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart", description = "Completely deletes a product from the cart by its ID.")
    public void delete(@PathVariable Long id) {
        cartService.removeProduct(id);
    }
}