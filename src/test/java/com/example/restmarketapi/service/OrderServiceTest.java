package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.CartItemResponseDto;
import com.example.restmarketapi.dto.OrderResponseDto;
import com.example.restmarketapi.entity.Cart;
import com.example.restmarketapi.entity.Order;
import com.example.restmarketapi.entity.OrderItem;
import com.example.restmarketapi.entity.Product;
import com.example.restmarketapi.entity.User;
import com.example.restmarketapi.repository.OrderRepository;
import com.example.restmarketapi.repository.ProductRepository;
import com.example.restmarketapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Long DEFAULT_ID = 1L;
    private static final Long ORDER_ID = 100L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password";
    private static final String PRODUCT_TITLE = "Sample Product";
    private static final String ERROR_MSG_PRODUCT_UNAVAILABLE = "One of your products is not available or doesn't exist";
    private static final int INITIAL_STOCK = 10;
    private static final int VALID_QUANTITY = 2;
    private static final int INSUFFICIENT_STOCK_QUANTITY = 15;
    private static final int EXPECTED_REMAINING_STOCK = 8;
    private static final int EXPECTED_ITEM_NUMBER = 1;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Cart cart;

    @InjectMocks
    private OrderService orderService;

    private Map<Long, Integer> cartItems;
    private User sampleUser;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        cartItems = new LinkedHashMap<>();
        sampleUser = new User(DEFAULT_ID, TEST_EMAIL, TEST_PASSWORD);
        sampleProduct = new Product();
        sampleProduct.setId(DEFAULT_ID);
        sampleProduct.setTitle(PRODUCT_TITLE);
        sampleProduct.setPrice(BigDecimal.TEN);
        sampleProduct.setAvailable(INITIAL_STOCK);
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartIsEmpty() {
        when(cart.getItems()).thenReturn(Collections.emptyMap());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(DEFAULT_ID));
        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserNotFound() {
        cartItems.put(DEFAULT_ID, VALID_QUANTITY);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(DEFAULT_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(DEFAULT_ID));
        verifyNoInteractions(productRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {
        cartItems.put(DEFAULT_ID, VALID_QUANTITY);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(DEFAULT_ID));
        assertEquals(ERROR_MSG_PRODUCT_UNAVAILABLE, exception.getMessage());
    }

    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        cartItems.put(DEFAULT_ID, INSUFFICIENT_STOCK_QUANTITY);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(sampleProduct));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(DEFAULT_ID));
        assertEquals(ERROR_MSG_PRODUCT_UNAVAILABLE, exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder_WhenDataIsValid() {
        cartItems.put(DEFAULT_ID, VALID_QUANTITY);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(DEFAULT_ID)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(sampleProduct));

        Order savedOrder = new Order();
        savedOrder.setId(ORDER_ID);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(sampleProduct);
        orderItem.setQuantity(VALID_QUANTITY);
        orderItem.setPriceAtPurchase(BigDecimal.TEN);
        savedOrder.setOrderItems(List.of(orderItem));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDto responseDto = new OrderResponseDto();
        CartItemResponseDto itemDto = new CartItemResponseDto();
        responseDto.setItems(new ArrayList<>(List.of(itemDto)));

        when(modelMapper.map(savedOrder, OrderResponseDto.class)).thenReturn(responseDto);

        OrderResponseDto result = orderService.createOrder(DEFAULT_ID);

        assertNotNull(result);
        assertEquals(EXPECTED_REMAINING_STOCK, sampleProduct.getAvailable());
        assertEquals(PRODUCT_TITLE, responseDto.getItems().get(0).getTitle());
        assertEquals(EXPECTED_ITEM_NUMBER, responseDto.getItems().get(0).getNumber());
        assertEquals(new BigDecimal("20"), responseDto.getItems().get(0).getSubTotal());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cart, times(1)).clearCart();
    }
}