package org.example.bookingapplication.dto.users.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.bookingapplication.validation.EmailValues;
import org.example.bookingapplication.validation.PasswordValues;

@Data
public class UserLoginRequestDto {
    @EmailValues
    @NotNull
    private String email;
    @PasswordValues
    private String password;
}
