package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    private UserController userController;

    private final UserRepository userRepository = mock(UserRepository.class);

    private final CartRepository cartRepository = mock(CartRepository.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() throws Exception {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void createUserSuccess() throws Exception {
        when(bCryptPasswordEncoder.encode("testPassword"))
                .thenReturn("thisIsHashed");

        CreateUserRequest req = new CreateUserRequest();
        req.setPassword("testPassword");
        req.setConfirmPassword("testPassword");
        req.setUsername("user");

        final ResponseEntity<User> resp = userController.createUser(req);

        assertNotNull(resp);
        assertEquals(200, resp.getStatusCodeValue());

        User user = resp.getBody();

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("user", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

        verify(bCryptPasswordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(any());
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    public void createUserPasswordTooShort() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setPassword("test");
        req.setConfirmPassword("test");
        req.setUsername("user");

        final ResponseEntity<User> resp = userController.createUser(req);

        assertNotNull(resp);
        assertEquals(400, resp.getStatusCodeValue());

        User user = resp.getBody();

        assertNull(user);

        verify(bCryptPasswordEncoder, times(0)).encode(any());
        verify(userRepository, times(0)).save(any());
        verify(cartRepository, times(0)).save(any());
    }


    @Test
    public void createUserPasswordDoesNotMatch() throws Exception {
        CreateUserRequest req = new CreateUserRequest();
        req.setPassword("testPassword");
        req.setConfirmPassword("testPassword2");
        req.setUsername("user");

        final ResponseEntity<User> resp = userController.createUser(req);

        assertNotNull(resp);
        assertEquals(400, resp.getStatusCodeValue());

        User user = resp.getBody();

        assertNull(user);

        verify(bCryptPasswordEncoder, times(0)).encode(any());
        verify(userRepository, times(0)).save(any());
        verify(cartRepository, times(0)).save(any());
    }

    @Test
    public void getUserById() throws Exception {
        User result = new User();
        result.setId(1L);
        result.setUsername("testUser");
        result.setPassword("testPassword");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(result));

        final ResponseEntity<User> resp = userController.findById(1L);

        assertNotNull(resp);
        assertEquals(200, resp.getStatusCodeValue());

        User user = resp.getBody();

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("testUser", user.getUsername());
        assertEquals("testPassword", user.getPassword());
    }

    @Test
    public void getUserByUsernameFound() {
        String username = "testuser";

        User result = new User();
        result.setId(1L);
        result.setUsername(username);
        result.setPassword("testPassword");

        when(userRepository.findByUsername(username))
                .thenReturn(result);

        final ResponseEntity<User> res = userController.findByUserName(username);

        assertEquals(200, res.getStatusCodeValue());

        User user = res.getBody();

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals(username, user.getUsername());
    }


    @Test
    public void getUserByUsernameNotFound() {
        String username = "testuser";
        User result = new User();
        result.setId(1L);
        result.setUsername(username);
        result.setPassword("testPassword");

        when(userRepository.findByUsername(username))
                .thenReturn(result);

        final ResponseEntity<User> res = userController.findByUserName("xxx");

        assertEquals(404, res.getStatusCodeValue());

        User user = res.getBody();

        assertNull(user);

        verify(userRepository, times(1)).findByUsername(any());
    }
}
