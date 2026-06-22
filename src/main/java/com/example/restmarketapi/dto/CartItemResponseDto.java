package com.example.restmarketapi.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {

    Integer number;
    String title;
    Integer quantity;
    BigDecimal price;
    BigDecimal subTotal;
}
