package com.countera.exercise.pricing;

/**
 * One priced line of a cart, all amounts in cents.
 *
 * @param sku            product SKU
 * @param name           product display name
 * @param quantity       units in the cart
 * @param unitPriceCents regular unit price
 * @param subtotalCents  quantity x unitPriceCents (before any promotion)
 * @param discountCents  amount taken off by a multi-buy promotion (0 if none)
 * @param taxCents       tax on (subtotal - discount), rounded HALF_UP to the cent
 */
public record PricedLine(
        String sku,
        String name,
        int quantity,
        long unitPriceCents,
        long subtotalCents,
        long discountCents,
        long taxCents) {

    /** Amount the customer pays for this line: subtotal - discount + tax. */
    public long totalCents() {
        return subtotalCents - discountCents + taxCents;
    }
}
