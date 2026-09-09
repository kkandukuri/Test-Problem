package com.countera.exercise.catalog;

import com.countera.exercise.model.Category;
import com.countera.exercise.model.MultiBuyPromotion;
import com.countera.exercise.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Hard-coded product and promotion data. In the real platform this comes from
 * the item-master service; here it is static so the exercise has no database.
 *
 * DO NOT MODIFY this class — the provided tests depend on these values.
 */
@Component
public class ProductCatalog {

    private static final List<Product> PRODUCTS = List.of(
            new Product("GRO-001", "White Bread",          299, Category.GROCERY,  false),
            new Product("GRO-002", "Milk 1 Gal",           449, Category.GROCERY,  false),
            new Product("BEV-001", "Energy Drink 16oz",    299, Category.BEVERAGE, false),
            new Product("BEV-002", "Bottled Water 20oz",   179, Category.BEVERAGE, false),
            new Product("TOB-001", "Cigarettes (pack)",    899, Category.TOBACCO,  true),
            new Product("ALC-001", "Beer 6-pack",         1099, Category.ALCOHOL,  true)
    );

    private static final List<MultiBuyPromotion> PROMOTIONS = List.of(
            new MultiBuyPromotion("BEV-001", 2, 500),   // 2 energy drinks for $5.00
            new MultiBuyPromotion("BEV-002", 3, 400)    // 3 waters for $4.00
    );

    private final Map<String, Product> productsBySku =
            PRODUCTS.stream().collect(Collectors.toMap(Product::sku, Function.identity()));

    private final Map<String, MultiBuyPromotion> promotionsBySku =
            PROMOTIONS.stream().collect(Collectors.toMap(MultiBuyPromotion::sku, Function.identity()));

    public Optional<Product> findProduct(String sku) {
        return Optional.ofNullable(productsBySku.get(sku));
    }

    public Optional<MultiBuyPromotion> findPromotion(String sku) {
        return Optional.ofNullable(promotionsBySku.get(sku));
    }
}
