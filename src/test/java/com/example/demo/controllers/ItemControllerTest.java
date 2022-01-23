package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.BeforeMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getItemsSuccess() {
        when(itemRepository.findAll())
                .thenReturn(getItems());

        ResponseEntity<List<Item>> resp = itemController.getItems();

        assertEquals(200, resp.getStatusCodeValue());

        List<Item> itemList = resp.getBody();

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
        assertEquals("firstItem", itemList.get(0).getName());
        assertEquals("secondItem", itemList.get(1).getName());
    }

    @Test
    public void getItemByIdSuccess() {
        Long targetId = 1L;
        when(itemRepository.findById(any()))
                .thenReturn(getItems()
                        .stream()
                        .filter(item -> Objects.equals(item.getId(), targetId))
                        .findFirst());

        ResponseEntity<Item> resp = itemController.getItemById(targetId);

        assertEquals(200, resp.getStatusCodeValue());

        Item item = resp.getBody();

        assertNotNull(item);
        assertEquals("firstItem", item.getName());
    }

    @Test
    public void getItemByIdNotFound() {
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        ResponseEntity<Item> resp = itemController.getItemById(1L);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    @Test
    public void getItemsByNameSuccess() {
        String foundName = "firstItem";
        when(itemRepository.findByName(any()))
                .thenReturn(getItems()
                        .stream()
                        .filter(item -> item.getName().equals(foundName))
                        .collect(Collectors.toList()));

        ResponseEntity<List<Item>> resp = itemController.getItemsByName(foundName);

        assertEquals(200, resp.getStatusCodeValue());

        List<Item> item = resp.getBody();

        assertNotNull(item);
        assertEquals(1, item.size());
        assertEquals("firstItem", item.get(0).getName());
    }

    @Test
    public void getItemsByNameNotFound() {
        String notFoundName = "notfound";
        when(itemRepository.findByName(any()))
                .thenReturn(getItems()
                        .stream()
                        .filter(item -> item.getName().equals(notFoundName))
                        .collect(Collectors.toList()));

        ResponseEntity<List<Item>> resp = itemController.getItemsByName(notFoundName);

        assertEquals(404, resp.getStatusCodeValue());
        assertNull(resp.getBody());
    }

    private List<Item> getItems() {
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

        List<Item> items = new ArrayList<>();
        items.add(firstItem);
        items.add(secondItem);

        return items;
    }
}
