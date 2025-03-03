package org.example.bookingapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.example.bookingapplication.dto.roletypes.response.RoleTypeDto;
import org.example.bookingapplication.dto.users.request.UserRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdateInfoRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdatePasswordRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdateRolesRequestDto;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.exception.repository.EntityAlreadyExistsException;
import org.example.bookingapplication.exception.repository.EntityNotFoundException;
import org.example.bookingapplication.exception.user.PasswordNotValidException;
import org.example.bookingapplication.mapper.UserMapper;
import org.example.bookingapplication.model.user.RoleType;
import org.example.bookingapplication.model.user.User;
import org.example.bookingapplication.repository.roletype.RoleTypeRepository;
import org.example.bookingapplication.repository.user.UserRepository;
import org.example.bookingapplication.testutil.UserSampleUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final RoleType.RoleName CUSTOMER_ROLE_TYPE = RoleType.RoleName.CUSTOMER;
    private static final RoleType.RoleName ADMIN_ROLE_TYPE = RoleType.RoleName.ADMIN;
    @Mock
    private RoleTypeRepository roleTypeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Get info about user with valid data")
    void getInfo_getUserInfoWithValidData_ReturnUser() {
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserResponseDto sampleUserResponseDto = UserSampleUtil.createSampleUserResponseDto(1L);

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponseDto);

        UserResponseDto result = userService.getInfo(sampleUser.getEmail());

        assertNotNull(result);
        assertEquals(sampleUserResponseDto, result);

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verify(userMapper, times(1)).toResponseDto(sampleUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Get info about user with non valid email")
    void getInfo_getUserInfoWithNonValidEmail_ThrowException() {
        String nonValidEmail = "nonValidEamil@i.com";
        when(userRepository.findUserByEmail(nonValidEmail)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getInfo(nonValidEmail));

        verify(userRepository, times(1)).findUserByEmail(nonValidEmail);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update info about user with valid data")
    void updateInfo_updateUserInfoWithValidData_ReturnUpdateUser() {
        UserUpdateInfoRequestDto sampleUserUpdateInfoRequestDto = UserSampleUtil
                .createSampleUserUpdateInfoRequestDto(1L);
        UserResponseDto sampleUserResponseDto = UserSampleUtil.createSampleUserResponseDto(1L);
        sampleUserResponseDto.setEmail(sampleUserUpdateInfoRequestDto.getEmail());
        sampleUserResponseDto.setFirstName(sampleUserUpdateInfoRequestDto.getFirstName());
        sampleUserResponseDto.setLastName(sampleUserUpdateInfoRequestDto.getLastName());
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        final String oldEmail = sampleUser.getEmail();

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(userRepository.findUserByEmail(sampleUserUpdateInfoRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        sampleUser.setFirstName(sampleUserUpdateInfoRequestDto.getFirstName());
        sampleUser.setLastName(sampleUserUpdateInfoRequestDto.getLastName());

        sampleUser.setEmail(sampleUserUpdateInfoRequestDto.getEmail());
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponseDto);

        UserResponseDto result = userService.updateInfo(oldEmail, sampleUserUpdateInfoRequestDto);
        assertNotNull(result);
        assertEquals(sampleUserResponseDto, result);

        verify(userRepository, times(1)).findUserByEmail(oldEmail);
        verify(userRepository, times(1))
                .findUserByEmail(sampleUserUpdateInfoRequestDto.getEmail());
        verify(userMapper,times(1))
                .setUpdateInfoToUser(sampleUser, sampleUserUpdateInfoRequestDto);
        verify(userRepository, times(1)).save(sampleUser);
        verify(userMapper, times(1)).toResponseDto(sampleUser);

        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update info about user with non valid email")
    void updateInfo_updateUserInfoWithNonValidEmail_ThrowException() {
        UserUpdateInfoRequestDto sampleUserUpdateInfoRequestDto = UserSampleUtil
                .createSampleUserUpdateInfoRequestDto(1L);
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUser.getEmail())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService
                .updateInfo(sampleUser.getEmail(), sampleUserUpdateInfoRequestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update info about user with exist email")
    void updateInfo_updateUserInfoWithExistEmail_ThrowException() {
        UserUpdateInfoRequestDto sampleUserUpdateInfoRequestDto = UserSampleUtil
                .createSampleUserUpdateInfoRequestDto(1L);
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(userRepository.findUserByEmail(sampleUserUpdateInfoRequestDto.getEmail()))
                .thenReturn(Optional.of(sampleUser));

        assertThrows(EntityAlreadyExistsException.class, () -> userService
                .updateInfo(sampleUser.getEmail(), sampleUserUpdateInfoRequestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verify(userRepository, times(1))
                .findUserByEmail(sampleUserUpdateInfoRequestDto.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Register user with valid data")
    void register_registerUserWithValidData_ReturnUser() {
        UserRequestDto sampleUserRequestDto = UserSampleUtil.createSampleUserRequestDto(1L);
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUserRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        when(userMapper.toModelWithoutPasswordAndRoles(sampleUserRequestDto))
                .thenReturn(sampleUser);
        sampleUser.setPassword(sampleUserRequestDto.getPassword());
        List<RoleType> roleTypeList = List.of(UserSampleUtil.createSampleRoleType());
        when(roleTypeRepository.findRoleTypesByNameIn(List.of(CUSTOMER_ROLE_TYPE)))
                .thenReturn(roleTypeList);
        sampleUser.setRoles(new HashSet<>(roleTypeList));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        UserResponseDto sampleUserResponseDto = UserSampleUtil.createSampleUserResponseDto(1L);
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponseDto);

        UserResponseDto result = userService.register(sampleUserRequestDto);

        assertNotNull(result);
        assertEquals(sampleUserResponseDto, result);

        verify(userRepository, times(1)).findUserByEmail(sampleUserRequestDto.getEmail());
        verify(userMapper,times(1)).toModelWithoutPasswordAndRoles(sampleUserRequestDto);
        verify(roleTypeRepository, times(1)).findRoleTypesByNameIn(List.of(CUSTOMER_ROLE_TYPE));
        verify(userRepository, times(1)).save(sampleUser);
        verify(userMapper, times(1)).toResponseDto(sampleUser);
        verifyNoMoreInteractions(userRepository, userMapper, roleTypeRepository);
    }

    @Test
    @DisplayName("Register user with exist email")
    void register_registerUserWithExistEmail_ThrowException() {
        UserRequestDto sampleUserRequestDto = UserSampleUtil.createSampleUserRequestDto(1L);
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUserRequestDto.getEmail()))
                .thenReturn(Optional.of(sampleUser));

        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.register(sampleUserRequestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUserRequestDto.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper, roleTypeRepository);
    }

    @Test
    @DisplayName("Register user with non valid password")
    void register_registerUserWithNonValidPassword_ThrowException() {
        UserRequestDto sampleUserRequestDto = UserSampleUtil.createSampleUserRequestDto(1L);
        sampleUserRequestDto.setRepeatPassword(sampleUserRequestDto.getPassword() + "test");
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUserRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        when(userMapper.toModelWithoutPasswordAndRoles(sampleUserRequestDto))
                .thenReturn(sampleUser);

        assertThrows(PasswordNotValidException.class,
                () -> userService.register(sampleUserRequestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUserRequestDto.getEmail());
        verify(userMapper,times(1)).toModelWithoutPasswordAndRoles(sampleUserRequestDto);
        verifyNoMoreInteractions(userRepository, userMapper, roleTypeRepository);
    }

    @Test
    @DisplayName("Update user password with valid data")
    void updatePassword_updateUserPasswordWithValidData_UpdateUserPassword() {
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserUpdatePasswordRequestDto requestDto = UserSampleUtil
                .createSampleUserUpdatePasswordRequestDto(sampleUser.getPassword());

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        sampleUser.setPassword(requestDto.getNewPassword());
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        userService.updatePassword(sampleUser.getEmail(), requestDto);

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verify(userRepository, times(1)).save(sampleUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update user password with valid data")
    void updatePassword_updateUserPasswordWithNotValidOldPassword_ThrowException() {
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserUpdatePasswordRequestDto requestDto = UserSampleUtil
                .createSampleUserUpdatePasswordRequestDto(sampleUser.getPassword() + "1234");

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(requestDto.getOldPassword(), sampleUser.getPassword()))
                .thenReturn(false);
        assertThrows(PasswordNotValidException.class,
                () -> userService.updatePassword(sampleUser.getEmail(), requestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update user password with not equals new passwords")
    void updatePassword_updateUserPasswordWithNotValidNewPassword_ThrowException() {
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserUpdatePasswordRequestDto requestDto = UserSampleUtil
                .createSampleUserUpdatePasswordRequestDto(sampleUser.getPassword());
        requestDto.setRepeatNewPassword(requestDto.getNewPassword() + "1234");

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(requestDto.getOldPassword(), sampleUser.getPassword()))
                .thenReturn(false);

        assertThrows(PasswordNotValidException.class,
                () -> userService.updatePassword(sampleUser.getEmail(), requestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update user roles with valid data")
    void updateRoles_updateUserRolesWithValidData_ReturnUser() {
        UserUpdateRolesRequestDto requestDto = new UserUpdateRolesRequestDto();
        requestDto.setRoleName(ADMIN_ROLE_TYPE);
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        sampleUser.setId(1L);
        RoleType customer = new RoleType();
        customer.setName(CUSTOMER_ROLE_TYPE);
        RoleType admin = new RoleType();
        admin.setName(ADMIN_ROLE_TYPE);
        RoleTypeDto customerDto = UserSampleUtil.createSampleRoleTypeDto();
        customerDto.setName(CUSTOMER_ROLE_TYPE.name());
        RoleTypeDto adminDto = UserSampleUtil.createSampleRoleTypeDto();
        adminDto.setName(ADMIN_ROLE_TYPE.name());
        List<RoleTypeDto> roleTypeDtoList = List.of(customerDto, adminDto);
        UserResponseDto sampleUserResponseDto = UserSampleUtil.createSampleUserResponseDto(1L);
        sampleUserResponseDto.setRoles(new HashSet<>(roleTypeDtoList));

        when(userRepository.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));
        List<RoleType> roleTypeList = List.of(customer, admin);
        List<RoleType.RoleName> roleNameList = List.of(CUSTOMER_ROLE_TYPE, ADMIN_ROLE_TYPE);
        when(roleTypeRepository.findRoleTypesByNameIn(roleNameList)).thenReturn(roleTypeList);
        sampleUser.setRoles(new HashSet<>(roleTypeList));
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponseDto);

        UserResponseDto result = userService.updateRoles(sampleUser.getId(), requestDto);

        assertNotNull(result);
        assertEquals(sampleUserResponseDto, result);

        verify(userRepository, times(1)).findById(sampleUser.getId());
        verify(roleTypeRepository, times(1)). findRoleTypesByNameIn(roleNameList);
        verify(userRepository, times(1)).save(sampleUser);
        verify(userMapper, times(1)).toResponseDto(sampleUser);
        verifyNoMoreInteractions(
                userRepository,
                roleTypeRepository,
                userMapper
        );
    }

    @Test
    @DisplayName("Update user roles with not valid id")
    void updateRoles_updateUserRolesWithValidId_ReturnUser() {
        UserUpdateRolesRequestDto requestDto = new UserUpdateRolesRequestDto();
        requestDto.setRoleName(ADMIN_ROLE_TYPE);

        when(userRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.updateRoles(-1L, requestDto));

        verify(userRepository, times(1)).findById(-1L);
        verifyNoMoreInteractions(
                userRepository,
                roleTypeRepository,
                userMapper
        );
    }
}
