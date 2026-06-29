package com.example.restmarketapi.entity;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@SessionScope
public class Cart {
    private Map<Long, Integer> items = new LinkedHashMap<>();

    public Map<Long, Integer> getItems() {
        return items;
    }

    public void clearCart() {
        items.clear();
    }
}
