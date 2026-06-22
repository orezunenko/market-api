package com.example.restmarketapi.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDto {

    List<CartItemResponseDto> items;
    BigDecimal totalSum;
}