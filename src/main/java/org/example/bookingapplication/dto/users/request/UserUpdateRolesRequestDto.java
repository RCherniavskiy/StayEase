package org.example.bookingapplication.dto.users.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.bookingapplication.model.user.RoleType;

@Data
public class UserUpdateRolesRequestDto {
    @NotNull
    private RoleType.RoleName roleName;
}
