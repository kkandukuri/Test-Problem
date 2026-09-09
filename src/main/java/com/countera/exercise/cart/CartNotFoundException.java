package com.countera.exercise.cart;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(String cartId) {
        super("Cart not found: " + cartId);
    }
}
