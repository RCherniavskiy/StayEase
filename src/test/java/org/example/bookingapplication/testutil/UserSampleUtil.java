package org.example.bookingapplication.testutil;

import java.util.HashSet;
import java.util.Set;
import org.example.bookingapplication.dto.roletypes.response.RoleTypeDto;
import org.example.bookingapplication.dto.users.request.UserRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdateInfoRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdatePasswordRequestDto;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.model.user.RoleType;
import org.example.bookingapplication.model.user.User;

public class UserSampleUtil {
    public static UserResponseDto createSampleUserResponseDto(Long param) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("test@example.com" + param);
        userResponseDto.setFirstName("John" + param);
        userResponseDto.setLastName("Doe" + param);
        userResponseDto.setRoles(new HashSet<>(Set.of(createSampleRoleTypeDto())));
        return userResponseDto;
    }

    public static UserRequestDto createSampleUserRequestDto(Long param) {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setEmail("test@example.com" + param);
        requestDto.setFirstName("John" + param);
        requestDto.setLastName("Doe" + param);
        requestDto.setPassword("Test@200@test");
        requestDto.setRepeatPassword("Test@200@test");
        return requestDto;
    }

    public static UserUpdateInfoRequestDto createSampleUserUpdateInfoRequestDto(Long param) {
        UserUpdateInfoRequestDto userResponseDto = new UserUpdateInfoRequestDto();
        userResponseDto.setEmail("testupd@example.com" + param);
        userResponseDto.setFirstName("JohnUpd" + param);
        userResponseDto.setLastName("DoeUpd" + param);
        return userResponseDto;
    }

    public static UserUpdatePasswordRequestDto createSampleUserUpdatePasswordRequestDto(
            String oldPassword) {
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();
        requestDto.setOldPassword(oldPassword);
        requestDto.setNewPassword("newPassword");
        requestDto.setRepeatNewPassword("newPassword");
        return requestDto;
    }

    public static User createSampleUser(Long param) {
        User user = new User();
        user.setEmail("test@example.com" + param);
        user.setFirstName("John" + param);
        user.setLastName("Doe" + param);
        user.setPassword("$2a$10$jQuBxj/mnQTEY60FgHeYiu8SahQYuj0O8shQtDMIAOAC1kYH3/2/6");
        user.setRoles(new HashSet<>(Set.of(createSampleRoleType())));
        return user;
    }

    public static RoleTypeDto createSampleRoleTypeDto() {
        RoleTypeDto roleTypeDto = new RoleTypeDto();
        roleTypeDto.setName("CUSTOMER");
        return roleTypeDto;
    }

    public static RoleType createSampleRoleType() {
        RoleType roleType = new RoleType();
        roleType.setName(RoleType.RoleName.CUSTOMER);
        return roleType;
    }
}
