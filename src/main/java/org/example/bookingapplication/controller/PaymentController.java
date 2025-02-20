package org.example.bookingapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.payment.responce.PaymentDto;
import org.example.bookingapplication.dto.payment.responce.PaymentInfoDto;
import org.example.bookingapplication.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payment management", description = "Endpoints manage payment")
@RestController
@RequestMapping(value = "/payments")
@RequiredArgsConstructor
public class
        PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/{id}")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get payment URL",
            description = "Get payment URL by booking id")
    public String createPaymentIntent(@PathVariable Long id, Authentication authentication) {
        return paymentService.createPaymentCheckoutSession(id, authentication.getName());
    }

    @GetMapping("/success/{sessionId}")
    @Operation(summary = "Success payment",
            description = "Success payment by session id")
    public PaymentDto successPayments(@PathVariable String sessionId) {
        return paymentService.successPayment(sessionId);
    }

    @GetMapping("/cancel/{sessionId}")
    @Operation(summary = "Cancel payment",
            description = "Cancel payment by session id")
    public PaymentDto cancelPayments(@PathVariable String sessionId) {
        return paymentService.cancelPaymentAndBooking(sessionId);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get all payments",
            description = "Get payment by user email")
    public List<PaymentInfoDto> getUserPayments(Authentication authentication) {
        return paymentService.findPaymentsByUserEmail(authentication.getName());
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all payments",
            description = "Get all payments for admin")
    public List<PaymentInfoDto> getAllPayments() {
        return paymentService.findAllPayments();
    }
}
