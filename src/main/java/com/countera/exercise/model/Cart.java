package com.countera.exercise.model;

import java.util.ArrayList;
import java.util.List;

/** Mutable in-memory shopping cart. One per open POS transaction. */
public class Cart {

    private final String id;
    private final List<CartItem> items = new ArrayList<>();

    public Cart(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    /** Live view of the items in insertion order. */
    public List<CartItem> getItems() {
        return items;
    }
}
