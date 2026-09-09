package com.countera.exercise.cart;

import com.countera.exercise.catalog.ProductCatalog;
import com.countera.exercise.model.Cart;
import org.springframework.stereotype.Service;

/**
 * In-memory cart store. No database — a Map is fine.
 *
 * TODO (candidate): implement the three methods.
 */
@Service
public class CartService {

    private final ProductCatalog catalog;

    public CartService(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    /** Creates an empty cart with a unique id and stores it. */
    public Cart create() {
        throw new UnsupportedOperationException("TODO");
    }

    /** @throws CartNotFoundException if no cart has this id */
    public Cart get(String cartId) {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Appends an item to the cart.
     *
     * @throws CartNotFoundException if no cart has this id
     * @throws UnknownSkuException   if the SKU is not in the catalog
     */
    public Cart addItem(String cartId, String sku, int quantity) {
        throw new UnsupportedOperationException("TODO");
    }
}
