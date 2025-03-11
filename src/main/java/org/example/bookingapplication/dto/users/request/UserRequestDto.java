package org.example.bookingapplication.dto.users.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.example.bookingapplication.validation.EmailValues;
import org.example.bookingapplication.validation.PasswordValues;

@Data
public class UserRequestDto {
    @EmailValues
    @NotBlank
    private String email;
    @PasswordValues
    private String password;
    @PasswordValues
    private String repeatPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
