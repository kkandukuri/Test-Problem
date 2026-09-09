package com.countera.exercise.model;

/**
 * A sellable item from the store catalog.
 *
 * All money in this exercise is represented as whole cents (long) to avoid
 * floating-point rounding problems. $2.49 is 249.
 *
 * @param sku           unique stock-keeping unit, e.g. "BEV-001"
 * @param name          display name
 * @param unitPriceCents regular shelf price per unit, in cents
 * @param category      tax/regulatory category
 * @param ageRestricted true if the cashier must verify the customer's age before selling
 */
public record Product(
        String sku,
        String name,
        long unitPriceCents,
        Category category,
        boolean ageRestricted) {
}
