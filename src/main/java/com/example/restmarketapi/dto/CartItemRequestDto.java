package com.example.restmarketapi.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequestDto {

    @NotNull
    private Long id;

    @NotNull
    @Min(1)
    private Integer quantity;
}
