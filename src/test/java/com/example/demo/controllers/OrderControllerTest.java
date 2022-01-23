package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void submitOrderSuccess() {
        when(userRepository.findByUsername(any()))
                .thenReturn(TestUtils.getUser());

        ResponseEntity<UserOrder> resp = orderController.submit("test");

        assertEquals(200, resp.getStatusCodeValue());

        UserOrder userOrder = resp.getBody();

        assertNotNull(userOrder);
        assertEquals(TestUtils.getCart().getItems(), userOrder.getItems());

        verify(orderRepository, times(1)).save(any());
    }

    @Test
    public void submitOrderNotFound() {
        when(userRepository.findByUsername(any()))
                .thenReturn(null);

        ResponseEntity<UserOrder> resp = orderController.submit("test");

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    public void getUserOrderByUsernameSuccess() {
        when(userRepository.findByUsername(any()))
                .thenReturn(TestUtils.getUser());
        when(orderRepository.findByUser(any()))
                .thenReturn(new ArrayList<UserOrder>() {{
                    add(UserOrder.createFromCart(TestUtils.getCart()));
                }});

        ResponseEntity<List<UserOrder>> resp = orderController.getOrdersForUser("test");

        assertEquals(200, resp.getStatusCodeValue());

        List<UserOrder> list = resp.getBody();

        assertNotNull(list);
        assertEquals(1, list.size());
    }


    @Test
    public void getUserOrderByUsernameNotFound() {
        when(userRepository.findByUsername(any()))
                .thenReturn(null);

        ResponseEntity<List<UserOrder>> resp = orderController.getOrdersForUser("test");

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());

        verify(orderRepository, times(0)).findByUser(any());
    }
}
