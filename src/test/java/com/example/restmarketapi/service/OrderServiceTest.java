package com.example.restmarketapi.service;

import com.example.restmarketapi.dto.CartItemResponseDto;
import com.example.restmarketapi.dto.OrderResponseDto;
import com.example.restmarketapi.entity.*;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

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
        sampleUser = new User(1L, "test@example.com", "password");
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setTitle("Sample Product");
        sampleProduct.setPrice(BigDecimal.TEN);
        sampleProduct.setAvailable(10);
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartIsEmpty() {
        when(cart.getItems()).thenReturn(Collections.emptyMap());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L));
        verifyNoInteractions(userRepository, productRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserNotFound() {
        cartItems.put(1L, 2);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L));
        verifyNoInteractions(productRepository, orderRepository);
    }

    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {
        cartItems.put(1L, 2);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L));
        assertEquals("One of your products is not available or doesn't exist", exception.getMessage());
    }

    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        cartItems.put(1L, 15);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(sampleProduct));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(1L));
        assertEquals("One of your products is not available or doesn't exist", exception.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_ShouldCreateAndReturnOrder_WhenDataIsValid() {
        cartItems.put(1L, 2);
        when(cart.getItems()).thenReturn(cartItems);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(sampleProduct));

        Order savedOrder = new Order();
        savedOrder.setId(100L);

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(sampleProduct);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(BigDecimal.TEN);
        savedOrder.setOrderItems(List.of(orderItem));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDto responseDto = new OrderResponseDto();
        CartItemResponseDto itemDto = new CartItemResponseDto();
        responseDto.setItems(new ArrayList<>(List.of(itemDto)));

        when(modelMapper.map(savedOrder, OrderResponseDto.class)).thenReturn(responseDto);

        OrderResponseDto result = orderService.createOrder(1L);

        assertNotNull(result);
        assertEquals(8, sampleProduct.getAvailable());
        assertEquals("Sample Product", responseDto.getItems().get(0).getTitle());
        assertEquals(1, responseDto.getItems().get(0).getNumber());
        assertEquals(new BigDecimal("20"), responseDto.getItems().get(0).getSubTotal());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cart, times(1)).clearCart();
    }
}