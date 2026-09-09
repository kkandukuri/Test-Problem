package com.countera.exercise.pricing;

import com.countera.exercise.catalog.ProductCatalog;
import com.countera.exercise.model.Cart;
import org.springframework.stereotype.Service;

/**
 * Prices a cart. See README.md for the rules.
 *
 * TODO (candidate): implement {@link #price(Cart)}.
 */
@Service
public class PricingService {

    private final ProductCatalog catalog;

    public PricingService(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    public CartTotal price(Cart cart) {
        throw new UnsupportedOperationException("TODO");
    }
}
