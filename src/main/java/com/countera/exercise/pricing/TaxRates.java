package com.countera.exercise.pricing;

import com.countera.exercise.model.Category;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Sales tax rate per category for the store's jurisdiction.
 * Tax is applied to the line amount AFTER promotions are taken off.
 *
 * DO NOT MODIFY — the provided tests depend on these values.
 */
public final class TaxRates {

    private static final Map<Category, BigDecimal> RATES = Map.of(
            Category.GROCERY,  new BigDecimal("0.0000"),  // groceries are tax-exempt
            Category.BEVERAGE, new BigDecimal("0.0725"),
            Category.TOBACCO,  new BigDecimal("0.1500"),
            Category.ALCOHOL,  new BigDecimal("0.1000")
    );

    private TaxRates() {
    }

    public static BigDecimal rateFor(Category category) {
        return RATES.get(category);
    }
}
