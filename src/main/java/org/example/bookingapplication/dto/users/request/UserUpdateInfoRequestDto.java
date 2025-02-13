package org.example.bookingapplication.dto.users.request;

import lombok.Data;
import org.example.bookingapplication.validation.EmailValues;

@Data
public class UserUpdateInfoRequestDto {
    @EmailValues
    private String email;
    private String firstName;
    private String lastName;
}
