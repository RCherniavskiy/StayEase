package org.example.bookingapplication.dto.users.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginResponseDto {
    private String token;
}
