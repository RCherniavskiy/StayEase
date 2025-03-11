package org.example.bookingapplication.dto.users.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.bookingapplication.validation.EmailValues;
import org.example.bookingapplication.validation.PasswordValues;

@Data
public class UserLoginRequestDto {
    @EmailValues
    @NotBlank
    private String email;
    @PasswordValues
    private String password;
}
