package org.example.bookingapplication.dto.users.response;

import java.util.Set;
import lombok.Data;
import org.example.bookingapplication.dto.roletypes.response.RoleTypeDto;

@Data
public class UserResponseDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Set<RoleTypeDto> roles;
}
