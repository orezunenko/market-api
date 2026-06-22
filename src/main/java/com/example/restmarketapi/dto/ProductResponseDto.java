package com.example.restmarketapi.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {

    private Long id;
    private String title;
    private BigDecimal price;
    private Integer available;
}
