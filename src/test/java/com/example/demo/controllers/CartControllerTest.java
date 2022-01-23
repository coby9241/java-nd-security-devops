package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeMethod;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartControllerTest {
    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addToCartSuccess() {
        when(userRepository.findByUsername(any()))
                .thenReturn(TestUtils.getUser());
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(TestUtils.getItem()));

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("testUser");
        req.setItemId(3L);
        req.setQuantity(2);

        final ResponseEntity<Cart> resp = cartController.addToCart(req);

        assertEquals(200, resp.getStatusCodeValue());

        Cart cart = resp.getBody();

        assertNotNull(cart);
        assertEquals(5, cart.getItems().size());
        assertEquals("firstItem", cart.getItems().get(0).getName());
        assertEquals("secondItem", cart.getItems().get(1).getName());
        assertEquals("secondItem", cart.getItems().get(2).getName());
        assertEquals("thirdItem", cart.getItems().get(3).getName());
        assertEquals("thirdItem", cart.getItems().get(4).getName());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void addToCartUserNotFound() {
        when(userRepository.findByUsername(any()))
                .thenReturn(null);

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("testUser");
        req.setItemId(3L);
        req.setQuantity(2);

        final ResponseEntity<Cart> resp = cartController.addToCart(req);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    public void addToCartItemNotFound() {
        when(userRepository.findByUsername(any()))
                .thenReturn(TestUtils.getUser());
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("testUser");
        req.setItemId(3L);
        req.setQuantity(2);

        final ResponseEntity<Cart> resp = cartController.addToCart(req);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }


    @Test
    public void removeFromCartSuccess() {
        when(userRepository.findByUsername(any()))
                .thenReturn(TestUtils.getUser());
        Item item = TestUtils.getItem();
        item.setId(2L);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("testUser");
        req.setItemId(2L);
        req.setQuantity(2);

        final ResponseEntity<Cart> resp = cartController.removeFromCart(req);

        assertEquals(200, resp.getStatusCodeValue());

        Cart cart = resp.getBody();

        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
        assertEquals("firstItem", cart.getItems().get(0).getName());

        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    public void removeFromCartUserNotFound() {
        when(userRepository.findByUsername(any()))
                .thenReturn(null);

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("testUser");
        req.setItemId(2L);
        req.setQuantity(2);

        final ResponseEntity<Cart> resp = cartController.removeFromCart(req);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    public void removeFromCartItemNotFound() {
        when(userRepository.findByUsername(any()))
                .thenReturn(TestUtils.getUser());
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        ModifyCartRequest req = new ModifyCartRequest();
        req.setUsername("testUser");
        req.setItemId(2L);
        req.setQuantity(2);

        final ResponseEntity<Cart> resp = cartController.removeFromCart(req);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }
}
