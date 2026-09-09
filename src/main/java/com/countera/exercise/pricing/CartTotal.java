package com.countera.exercise.pricing;

import java.util.List;

/**
 * The fully priced cart. Header amounts are the sums of the line amounts.
 *
 * @param ageVerificationRequired true if ANY line contains an age-restricted product
 */
public record CartTotal(
        List<PricedLine> lines,
        long subtotalCents,
        long discountCents,
        long taxCents,
        long totalCents,
        boolean ageVerificationRequired) {
}
