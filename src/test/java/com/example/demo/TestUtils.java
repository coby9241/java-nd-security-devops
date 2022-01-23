package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class TestUtils {

    public static void injectObject(Object target, String fieldName, Object toInject)
            throws Exception {
        boolean wasPrivate = false;
        try {
            Field targetField = target.getClass().getDeclaredField(fieldName);
            if (!targetField.isAccessible()) {
                targetField.setAccessible(true);
                wasPrivate = true;
            }

            targetField.set(target, toInject);
            if (wasPrivate) {
                targetField.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User getUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        Cart cart = getCart();
        cart.setUser(user);
        user.setCart(getCart());
        return user;
    }

    public static Item getItem() {
        Item item = new Item();
        item.setId(3L);
        item.setDescription("Item description");
        item.setName("thirdItem");
        item.setPrice(BigDecimal.valueOf(12.0));
        return item;
    }

    public static Cart getCart() {
        Cart cart = new Cart();

        Item firstItem = new Item();
        firstItem.setId(1L);
        firstItem.setDescription("Item description");
        firstItem.setName("firstItem");
        firstItem.setPrice(BigDecimal.valueOf(20.0));

        Item secondItem = new Item();
        secondItem.setId(2L);
        secondItem.setDescription("Item description");
        secondItem.setName("secondItem");
        secondItem.setPrice(BigDecimal.valueOf(35.1));

        cart.addItem(firstItem);
        cart.addItem(secondItem);
        cart.addItem(secondItem);

        return cart;
    }
}
