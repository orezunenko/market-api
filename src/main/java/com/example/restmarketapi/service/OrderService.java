package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.OrderResponseDto;
import com.example.restmarketapi.entity.Cart;
import com.example.restmarketapi.entity.Order;
import com.example.restmarketapi.entity.OrderItem;
import com.example.restmarketapi.entity.OrderStatus;
import com.example.restmarketapi.entity.Product;
import com.example.restmarketapi.entity.User;
import com.example.restmarketapi.repository.OrderRepository;
import com.example.restmarketapi.repository.ProductRepository;
import com.example.restmarketapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String EMPTY_CART_MSG = "You have empty cart";
    private static final String USER_NOT_FOUND_MSG = "User not found";
    private static final String PRODUCT_NOT_AVAILABLE_MSG = "One of your products is not available or doesn't exist";
    private static final String UNAUTHORIZED_MSG = "User is not authenticated. Please log in first.";

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final Cart cart;

    @Transactional
    public OrderResponseDto createOrder(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException(UNAUTHORIZED_MSG);

        }
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException(EMPTY_CART_MSG);
        }

        Map<Long, Integer> mapOfItems = new LinkedHashMap<>(cart.getItems());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND_MSG));

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
                throw new IllegalArgumentException(PRODUCT_NOT_AVAILABLE_MSG);
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
