package com.countera.exercise.api;

import com.countera.exercise.cart.CartService;
import com.countera.exercise.cart.CartNotFoundException;
import com.countera.exercise.cart.UnknownSkuException;
import com.countera.exercise.pricing.PricingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST endpoints for the cart. See README.md for the contract.
 */
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;
    private final PricingService pricingService;

    public CartController(CartService cartService, PricingService pricingService) {
        this.cartService = cartService;
        this.pricingService = pricingService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createCart() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("cartId", cartService.create().getId()));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<com.countera.exercise.model.Cart> addItem(
            @PathVariable String cartId, @Valid @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(cartId, request.sku(), request.quantity()));
    }

    @GetMapping("/{cartId}/total")
    public com.countera.exercise.pricing.CartTotal total(@PathVariable String cartId) {
        return pricingService.price(cartService.get(cartId));
    }

    @ExceptionHandler({CartNotFoundException.class, UnknownSkuException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        String message = fieldError == null ? "Invalid request" : fieldError.getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse("BAD_REQUEST", message));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("BAD_REQUEST", "Request body is invalid"));
    }
}
