Implemented the in-memory cart workflow and REST endpoints described in the README.
Pricing groups duplicate SKUs while preserving first-seen line order.
Multi-buy discounts apply only to complete promotion groups.
Tax is calculated per line with HALF_UP cent rounding.
Unknown carts and SKUs return the required NOT_FOUND error response.
Invalid item requests return the required BAD_REQUEST error response.
Added tests for promotion leftovers and line ordering.
