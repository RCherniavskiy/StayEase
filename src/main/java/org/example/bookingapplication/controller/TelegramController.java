package org.example.bookingapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.service.TelegramService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Telegram management", description = "Endpoints manage telegram bot")
@RestController
@RequestMapping(value = "/telegram")
@RequiredArgsConstructor
public class TelegramController {
    private final TelegramService telegramService;

    @GetMapping("/invite")
    @PreAuthorize("hasAuthority('CUSTOMER')")
    @Operation(summary = "Get telegram invite",
            description = "Get uniq url for invite to telegram bot")
    public String getUrl(Authentication authentication) {
        return telegramService.getUrl(authentication.getName());
    }
}
