package com.countera.exercise.pricing;

import com.countera.exercise.catalog.ProductCatalog;
import com.countera.exercise.cart.UnknownSkuException;
import com.countera.exercise.model.Cart;
import com.countera.exercise.model.CartItem;
import com.countera.exercise.model.Product;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prices a cart. See README.md for the rules.
 */
@Service
public class PricingService {

    private final ProductCatalog catalog;

    public PricingService(ProductCatalog catalog) {
        this.catalog = catalog;
    }

    public CartTotal price(Cart cart) {
        Map<String, Integer> quantitiesBySku = new LinkedHashMap<>();
        for (CartItem item : cart.getItems()) {
            quantitiesBySku.merge(item.sku(), item.quantity(), Integer::sum);
        }

        List<PricedLine> lines = new ArrayList<>();
        long subtotal = 0;
        long discount = 0;
        long tax = 0;
        boolean ageVerificationRequired = false;

        for (Map.Entry<String, Integer> entry : quantitiesBySku.entrySet()) {
            Product product = catalog.findProduct(entry.getKey())
                    .orElseThrow(() -> new UnknownSkuException(entry.getKey()));
            int quantity = entry.getValue();
            long lineSubtotal = Math.multiplyExact((long) quantity, product.unitPriceCents());
            long lineDiscount = discountFor(product, quantity);
            long taxableAmount = lineSubtotal - lineDiscount;
            long lineTax = BigDecimal.valueOf(taxableAmount)
                    .multiply(TaxRates.rateFor(product.category()))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact();

            lines.add(new PricedLine(product.sku(), product.name(), quantity,
                    product.unitPriceCents(), lineSubtotal, lineDiscount, lineTax));
            subtotal = Math.addExact(subtotal, lineSubtotal);
            discount = Math.addExact(discount, lineDiscount);
            tax = Math.addExact(tax, lineTax);
            ageVerificationRequired |= product.ageRestricted();
        }

        return new CartTotal(lines, subtotal, discount, tax,
                subtotal - discount + tax, ageVerificationRequired);
    }

    private long discountFor(Product product, int quantity) {
        return catalog.findPromotion(product.sku())
                .map(promotion -> {
                    int groups = quantity / promotion.quantity();
                    long promotedUnits = Math.multiplyExact((long) groups, promotion.quantity());
                    long regularPrice = Math.multiplyExact(promotedUnits, product.unitPriceCents());
                    long promotionPrice = Math.multiplyExact((long) groups, promotion.groupPriceCents());
                    return regularPrice - promotionPrice;
                })
                .orElse(0L);
    }
}
