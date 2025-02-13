package org.example.bookingapplication.dto.users.request;

import lombok.Data;
import org.example.bookingapplication.validation.PasswordValues;

@Data
public class UserUpdatePasswordRequestDto {
    @PasswordValues
    private String oldPassword;
    @PasswordValues
    private String newPassword;
    @PasswordValues
    private String repeatNewPassword;
}
