package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.ProductResponseDto;
import com.example.restmarketapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    public List<ProductResponseDto> getAll() {
        return productRepository.findAll().stream()
                .map(product -> modelMapper.map(product, ProductResponseDto.class))
                .toList();

    }
}
