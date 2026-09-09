# Countera – Junior Developer Take-Home: POS Cart Pricing

Thanks for taking the time. This exercise is a small slice of what our point-of-sale
does at a convenience-store checkout: build a cart, add scanned items, and compute what
the customer owes. It should take **2–3 hours**. Please don't spend more than 4.

## What you get

A Spring Boot 3 / Java 17 Maven project that compiles but does nothing yet.

| Already written (don't change) | You implement |
|---|---|
| `model/*` – `Product`, `Cart`, `CartItem`, `MultiBuyPromotion` | `pricing/PricingService.price(Cart)` |
| `catalog/ProductCatalog` – 6 products, 2 promotions | `cart/CartService` – in-memory cart storage |
| `pricing/TaxRates` – tax rate per category | `api/CartController` – 3 REST endpoints + error handling |
| `pricing/PricedLine`, `pricing/CartTotal` – the result shape | your own additional tests |
| `api/AddItemRequest`, `api/ErrorResponse` | |
| `src/test/**` – 12 tests that must pass | |

All money is in **whole cents as `long`** (`$2.99` → `299`). Don't use `double` for money.

## Pricing rules

1. **Lines.** Group the cart by SKU (the same SKU scanned twice is one line with the
   combined quantity). Keep the order in which SKUs were first added.
2. **Subtotal** of a line = `quantity × unitPriceCents`.
3. **Multi-buy promotion** ("2 for $5.00"). If the catalog has a promotion for the SKU,
   every *complete* group of `promotion.quantity` units is charged `groupPriceCents`
   instead of the regular price. Leftover units pay the regular price.
   `discountCents` = regular price of the promo'd units − promo price.
   Example: unit 299, "2 for 500", quantity 5 → 2 groups → discount `(4×299) − (2×500) = 196`.
4. **Tax** is computed per line on `subtotal − discount`, using `TaxRates.rateFor(category)`,
   rounded **HALF_UP** to the cent. Groceries are 0%.
5. **Header totals** are the sums of the line values; `totalCents = subtotal − discount + tax`.
6. **`ageVerificationRequired`** is `true` if any product in the cart has `ageRestricted == true`.
7. An unknown SKU in a cart throws `UnknownSkuException`.

## REST contract

| Method | Path | Body | Success | Errors |
|---|---|---|---|---|
| `POST` | `/carts` | – | `201` `{"cartId": "..."}` | |
| `POST` | `/carts/{cartId}/items` | `{"sku": "BEV-001", "quantity": 2}` | `200` the cart (id + items) | `404` unknown cart or SKU · `400` missing sku / quantity < 1 |
| `GET` | `/carts/{cartId}/total` | – | `200` `CartTotal` as JSON | `404` unknown cart |

Error bodies use `ErrorResponse`: `{"error": "NOT_FOUND", "message": "..."}` or
`{"error": "BAD_REQUEST", "message": "..."}`.

Sample `GET /carts/{id}/total` response:

```json
{
  "lines": [
    { "sku": "BEV-001", "name": "Energy Drink 16oz", "quantity": 2,
      "unitPriceCents": 299, "subtotalCents": 598, "discountCents": 98, "taxCents": 36 }
  ],
  "subtotalCents": 598,
  "discountCents": 98,
  "taxCents": 36,
  "totalCents": 536,
  "ageVerificationRequired": false
}
```

## Running

```bash
mvn test                 # the provided tests — all must pass when you're done
mvn spring-boot:run      # then try it:
curl -X POST localhost:8080/carts
curl -X POST localhost:8080/carts/<id>/items -H 'Content-Type: application/json' -d '{"sku":"BEV-001","quantity":2}'
curl localhost:8080/carts/<id>/total
```

## What we look at

Roughly in this order: the provided tests pass; the code is easy to read; edge cases are
handled (empty cart, leftover units, rounding, duplicate SKUs); you added tests of your
own that cover something the provided ones don't; error responses are correct and
consistent. We are **not** looking for a database, security, Docker, or a UI.

## Submitting

Zip the project (without `target/`) or push it to a private Git repo and share the link.
Add a short `NOTES.md` — 5 to 10 lines — with anything you'd want us to know: assumptions,
what you'd do next with more time, anything you found unclear.

Questions are welcome; email the person who sent you this.
