package org.example.bookingapplication.testutil;

import java.util.Set;
import org.example.bookingapplication.dto.roletypes.response.RoleTypeDto;
import org.example.bookingapplication.dto.users.request.UserUpdateInfoRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdatePasswordRequestDto;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.model.user.RoleType;

public class UserControllerDtoUtil {
    public static UserResponseDto getUserResponseDtoFromDb() {
        RoleTypeDto customerRoleTypeDto = new RoleTypeDto();
        customerRoleTypeDto.setId(1L);
        customerRoleTypeDto.setName(RoleType.RoleName.CUSTOMER.name());

        RoleTypeDto adminRoleTypeDto = new RoleTypeDto();
        adminRoleTypeDto.setId(2L);
        adminRoleTypeDto.setName(RoleType.RoleName.ADMIN.name());

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(3L);
        responseDto.setEmail("testUser1@testmail.com");
        responseDto.setFirstName("Test1");
        responseDto.setLastName("Name1");
        responseDto.setRoles(Set.of(customerRoleTypeDto, adminRoleTypeDto));

        return responseDto;
    }

    public static UserUpdateInfoRequestDto getUserUpdateInfoRequestDto() {
        UserUpdateInfoRequestDto requestDto = new UserUpdateInfoRequestDto();
        requestDto.setEmail("testUser1@testmailUpd.com");
        requestDto.setFirstName("UpdFirst");
        requestDto.setLastName("UpdLast");

        return requestDto;
    }

    public static UserUpdatePasswordRequestDto getUserUpdatePasswordRequestDto() {
        UserUpdatePasswordRequestDto requestDto = new UserUpdatePasswordRequestDto();
        requestDto.setOldPassword("admADM200@");
        requestDto.setNewPassword("UPDupd@002");
        requestDto.setRepeatNewPassword("UPDupd@002");

        return requestDto;
    }
}
