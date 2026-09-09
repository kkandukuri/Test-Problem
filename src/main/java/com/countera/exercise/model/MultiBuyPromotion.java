package com.countera.exercise.model;

/**
 * "N for $X" shelf promotion, very common in convenience stores
 * (e.g. "2 energy drinks for $5.00").
 *
 * Every complete group of {@code quantity} units is sold for {@code groupPriceCents}.
 * Units beyond the last complete group are charged at the regular unit price.
 *
 * Example: unit price 299, promotion 2 for 500, customer buys 5 units
 *   -> 2 groups x 500 + 1 x 299 = 1299
 */
public record MultiBuyPromotion(String sku, int quantity, long groupPriceCents) {
}
