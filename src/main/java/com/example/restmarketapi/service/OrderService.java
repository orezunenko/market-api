package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.OrderResponseDto;
import com.example.restmarketapi.entity.*;
import com.example.restmarketapi.repository.OrderRepository;
import com.example.restmarketapi.repository.ProductRepository;
import com.example.restmarketapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Cart cart;


    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, ModelMapper modelMapper, Cart cart) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.cart = cart;
    }

    @Transactional
    public OrderResponseDto createOrder(Long userId) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("You have empty cart");
        }

        Map<Long, Integer> mapOfItems = new LinkedHashMap<>(cart.getItems());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Long> productIds = new ArrayList<>(mapOfItems.keySet());
        List<Product> productsFromDb = productRepository.findAllById(productIds);

        Map<Long, Product> productMap = new HashMap<>();
        for (Product p : productsFromDb) {
            productMap.put(p.getId(), p);
        }

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUser(user);

        BigDecimal totalSum = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : mapOfItems.entrySet()) {
            Long productId = entry.getKey();
            Integer quantity = entry.getValue();

            Product product = productMap.get(productId);

            if (product == null || product.getAvailable() < quantity) {
                throw new IllegalArgumentException("One of your products is not available or doesn't exist");
            }

            product.setAvailable(product.getAvailable() - quantity);

            BigDecimal itemPrice = product.getPrice();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));
            totalSum = totalSum.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);
            orderItem.setPriceAtPurchase(itemPrice);
            items.add(orderItem);
        }

        order.setTotalSum(totalSum);
        order.setOrderItems(items);
        order.setStatus(OrderStatus.SUCCESS);

        Order savedOrder = orderRepository.save(order);

        OrderResponseDto responseDto = modelMapper.map(savedOrder, OrderResponseDto.class);

        for (int i = 0; i < savedOrder.getOrderItems().size(); i++) {
            OrderItem entityItem = savedOrder.getOrderItems().get(i);
            var dtoItem = responseDto.getItems().get(i);

            Product product = productMap.get(entityItem.getProduct().getId());
            if (product != null) {
                dtoItem.setTitle(product.getTitle());
                dtoItem.setNumber(product.getId().intValue());
            }

            BigDecimal subTotal = entityItem.getPriceAtPurchase()
                    .multiply(BigDecimal.valueOf(entityItem.getQuantity()));
            dtoItem.setSubTotal(subTotal);
        }

        cart.clearCart();

        return responseDto;
    }
}
