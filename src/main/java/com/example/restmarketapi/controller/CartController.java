package com.example.restmarketapi.controller;

import com.example.restmarketapi.dto.CartItemRequestDto;
import com.example.restmarketapi.dto.CartResponseDto;
import com.example.restmarketapi.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Manage products in the shopping cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get cart contents", description = "Returns all items in the cart and the total price.")
    public ResponseEntity<CartResponseDto> getAll() {
        CartResponseDto cartResponseDto = cartService.getAll();
        return ResponseEntity.ok(cartResponseDto);
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product or increases its quantity if already in the cart.")
    public ResponseEntity<String> addProductToCart(@RequestBody CartItemRequestDto requestDto) {
        cartService.addProductToCart(requestDto.getId(), requestDto.getQuantity());
        return ResponseEntity.ok("You successfully add product to cart");
    }

    @PutMapping("/items")
    @Operation(summary = "Update item quantity", description = "Sets a new exact quantity for a specific cart item.")
    public ResponseEntity<String> changeNumberOfProduct(@RequestBody CartItemRequestDto requestDto) {
        cartService.changeNumberOfProduct(requestDto.getId(), requestDto.getQuantity());
        return ResponseEntity.ok("Number of product successfully changed to: " + requestDto.getQuantity());
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart", description = "Completely deletes a product from the cart by its ID.")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        cartService.removeProduct(id);
        return ResponseEntity.ok("Product successfully deleted from cart");
    }
}