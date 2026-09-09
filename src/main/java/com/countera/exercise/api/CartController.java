package com.countera.exercise.api;

import com.countera.exercise.cart.CartService;
import com.countera.exercise.pricing.PricingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoints for the cart. See README.md for the contract.
 *
 * TODO (candidate): implement the three endpoints and the error handling
 * (404 for unknown cart / SKU, 400 for an invalid request body).
 * You may add more classes in this package.
 */
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final PricingService pricingService;

    public CartController(CartService cartService, PricingService pricingService) {
        this.cartService = cartService;
        this.pricingService = pricingService;
    }
}
