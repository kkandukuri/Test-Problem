package com.countera.exercise.cart;

import com.countera.exercise.catalog.ProductCatalog;
import com.countera.exercise.model.Cart;
import com.countera.exercise.model.CartItem;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory cart store. No database — a Map is fine.
 */
@Service
public class CartService {

    private final ProductCatalog catalog;
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();

    public CartService(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    /** Creates an empty cart with a unique id and stores it. */
    public Cart create() {
        Cart cart = new Cart(UUID.randomUUID().toString());
        carts.put(cart.getId(), cart);
        return cart;
    }

    /** @throws CartNotFoundException if no cart has this id */
    public Cart get(String cartId) {
        Cart cart = carts.get(cartId);
        if (cart == null) {
            throw new CartNotFoundException(cartId);
        }
        return cart;
    }

    /**
     * Appends an item to the cart.
     *
     * @throws CartNotFoundException if no cart has this id
     * @throws UnknownSkuException   if the SKU is not in the catalog
     */
    public Cart addItem(String cartId, String sku, int quantity) {
        Cart cart = get(cartId);
        if (catalog.findProduct(sku).isEmpty()) {
            throw new UnknownSkuException(sku);
        }
        for (int index = 0; index < cart.getItems().size(); index++) {
            CartItem existingItem = cart.getItems().get(index);
            if (existingItem.sku().equals(sku)) {
                cart.getItems().set(index,
                        new CartItem(sku, Math.addExact(existingItem.quantity(), quantity)));
                return cart;
            }
        }
        cart.getItems().add(new CartItem(sku, quantity));
        return cart;
    }
}
