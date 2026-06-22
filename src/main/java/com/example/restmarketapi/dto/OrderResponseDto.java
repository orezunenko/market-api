package com.example.restmarketapi.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {

    private Long id;
    private BigDecimal totalSum;
    private String status;
    private LocalDateTime createdAt;
    private List<CartItemResponseDto> items;

}
