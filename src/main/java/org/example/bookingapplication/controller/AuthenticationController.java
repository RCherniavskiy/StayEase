package org.example.bookingapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookingapplication.dto.users.request.UserLoginRequestDto;
import org.example.bookingapplication.dto.users.request.UserRequestDto;
import org.example.bookingapplication.dto.users.response.UserLoginResponseDto;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.security.AuthenticationService;
import org.example.bookingapplication.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth management", description = "Endpoints to authorisation")
@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Register user", description = "Register a new user")
    public UserResponseDto register(@RequestBody @Valid UserRequestDto requestDto) {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Login user")
    public UserLoginResponseDto login(@RequestBody UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
