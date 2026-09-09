package com.countera.exercise.pricing;

import com.countera.exercise.catalog.ProductCatalog;
import com.countera.exercise.cart.UnknownSkuException;
import com.countera.exercise.model.Cart;
import com.countera.exercise.model.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Provided tests. All of these must pass. Add your own tests below the marker.
 */
class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService(new ProductCatalog());
    }

    private static Cart cartOf(CartItem... items) {
        Cart cart = new Cart("test");
        for (CartItem item : items) {
            cart.getItems().add(item);
        }
        return cart;
    }

    @Test
    void emptyCartTotalsToZero() {
        CartTotal total = pricingService.price(cartOf());

        assertThat(total.lines()).isEmpty();
        assertThat(total.totalCents()).isZero();
        assertThat(total.ageVerificationRequired()).isFalse();
    }

    @Test
    void groceryIsTaxExempt() {
        // 2 x bread @ 299 = 598, no promo, 0% tax
        CartTotal total = pricingService.price(cartOf(new CartItem("GRO-001", 2)));

        assertThat(total.subtotalCents()).isEqualTo(598);
        assertThat(total.discountCents()).isZero();
        assertThat(total.taxCents()).isZero();
        assertThat(total.totalCents()).isEqualTo(598);
    }

    @Test
    void beverageTaxIsRoundedHalfUpToTheCent() {
        // 1 x water @ 179, tax 7.25% = 12.9775 -> 13
        CartTotal total = pricingService.price(cartOf(new CartItem("BEV-002", 1)));

        assertThat(total.taxCents()).isEqualTo(13);
        assertThat(total.totalCents()).isEqualTo(192);
    }

    @Test
    void multiBuyPromotionAppliesToCompleteGroupsOnly() {
        // 5 x energy drink @ 299 = 1495; promo 2 for 500
        // 2 groups: regular 1196 -> promo 1000, discount 196; 1 leftover at 299
        // taxable = 1495 - 196 = 1299; tax 7.25% = 94.18 -> 94
        CartTotal total = pricingService.price(cartOf(new CartItem("BEV-001", 5)));

        PricedLine line = total.lines().get(0);
        assertThat(line.subtotalCents()).isEqualTo(1495);
        assertThat(line.discountCents()).isEqualTo(196);
        assertThat(line.taxCents()).isEqualTo(94);
        assertThat(total.totalCents()).isEqualTo(1393);
    }

    @Test
    void sameSkuAddedTwiceIsMergedBeforePromotion() {
        // Customer scans one energy drink, then another: promo must still apply.
        CartTotal total = pricingService.price(cartOf(
                new CartItem("BEV-001", 1),
                new CartItem("BEV-001", 1)));

        assertThat(total.lines()).hasSize(1);
        assertThat(total.lines().get(0).quantity()).isEqualTo(2);
        assertThat(total.discountCents()).isEqualTo(98);
    }

    @Test
    void ageRestrictedItemFlagsTheWholeCart() {
        CartTotal total = pricingService.price(cartOf(
                new CartItem("GRO-002", 1),
                new CartItem("TOB-001", 1)));

        assertThat(total.ageVerificationRequired()).isTrue();
    }

    @Test
    void headerAmountsAreSumOfLines() {
        CartTotal total = pricingService.price(cartOf(
                new CartItem("GRO-001", 1),   // 299, tax 0
                new CartItem("BEV-002", 3),   // 537, promo 3 for 400 -> discount 137, tax on 400 = 29
                new CartItem("ALC-001", 1))); // 1099, tax 10% = 110 (109.9 -> 110)

        assertThat(total.subtotalCents()).isEqualTo(299 + 537 + 1099);
        assertThat(total.discountCents()).isEqualTo(137);
        assertThat(total.taxCents()).isEqualTo(29 + 110);
        assertThat(total.totalCents())
                .isEqualTo(total.subtotalCents() - total.discountCents() + total.taxCents());
    }

    @Test
    void unknownSkuIsRejected() {
        assertThatThrownBy(() -> pricingService.price(cartOf(new CartItem("NOPE-999", 1))))
                .isInstanceOf(UnknownSkuException.class);
    }

    // ---- Add your own tests below this line -------------------------------------
}
