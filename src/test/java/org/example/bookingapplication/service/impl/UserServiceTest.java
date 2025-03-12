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
        // Given: A user and their response DTO
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserResponseDto sampleUserResponseDto = UserSampleUtil.createSampleUserResponseDto(1L);

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(userMapper.toResponseDto(sampleUser)).thenReturn(sampleUserResponseDto);

        // When: The method to get user info is called
        UserResponseDto result = userService.getInfo(sampleUser.getEmail());

        // Then: Ensure the result is not null and matches the expected DTO
        assertNotNull(result);
        assertEquals(sampleUserResponseDto, result);

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verify(userMapper, times(1)).toResponseDto(sampleUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Get info about user with non valid email")
    void getInfo_getUserInfoWithNonValidEmail_ThrowException() {
        // Given: An invalid email
        String nonValidEmail = "nonValidEamil@i.com";
        when(userRepository.findUserByEmail(nonValidEmail)).thenReturn(Optional.empty());

        // When & Then: Expect an exception
        assertThrows(EntityNotFoundException.class,
                () -> userService.getInfo(nonValidEmail));

        verify(userRepository, times(1)).findUserByEmail(nonValidEmail);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update info about user with valid data")
    void updateInfo_updateUserInfoWithValidData_ReturnUpdateUser() {
        // Given: An existing user and new update data
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

        // When: The update method is called
        UserResponseDto result = userService.updateInfo(oldEmail, sampleUserUpdateInfoRequestDto);

        // Then: Ensure the result is not null and matches the expected DTO
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
        // Given: A user update request and a non-existing user
        UserUpdateInfoRequestDto sampleUserUpdateInfoRequestDto = UserSampleUtil
                .createSampleUserUpdateInfoRequestDto(1L);
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUser.getEmail())).thenReturn(Optional.empty());

        // When & Then: Expect an exception when trying to update a non-existing user
        assertThrows(EntityNotFoundException.class, () -> userService
                .updateInfo(sampleUser.getEmail(), sampleUserUpdateInfoRequestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update info about user with exist email")
    void updateInfo_updateUserInfoWithExistEmail_ThrowException() {
        // Given: A user update request where the new email already exists
        UserUpdateInfoRequestDto sampleUserUpdateInfoRequestDto = UserSampleUtil
                .createSampleUserUpdateInfoRequestDto(1L);
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(userRepository.findUserByEmail(sampleUserUpdateInfoRequestDto.getEmail()))
                .thenReturn(Optional.of(sampleUser));

        // When & Then: Expect an exception when trying to update to an already existing email
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
        // Given: A valid user registration request
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

        // When: The user is registered
        UserResponseDto result = userService.register(sampleUserRequestDto);

        // Then: The user is successfully registered and returned
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
        // Given: A sample user request DTO and an existing user in the database
        UserRequestDto sampleUserRequestDto = UserSampleUtil.createSampleUserRequestDto(1L);
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUserRequestDto.getEmail()))
                .thenReturn(Optional.of(sampleUser));

        // When & Then: Attempting to register should throw an EntityAlreadyExistsException
        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.register(sampleUserRequestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUserRequestDto.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper, roleTypeRepository);
    }

    @Test
    @DisplayName("Register user with non valid password")
    void register_registerUserWithNonValidPassword_ThrowException() {
        // Given: A sample user request DTO with mismatched passwords
        UserRequestDto sampleUserRequestDto = UserSampleUtil.createSampleUserRequestDto(1L);
        sampleUserRequestDto.setRepeatPassword(sampleUserRequestDto.getPassword() + "test");
        User sampleUser = UserSampleUtil.createSampleUser(1L);

        when(userRepository.findUserByEmail(sampleUserRequestDto.getEmail()))
                .thenReturn(Optional.empty());
        when(userMapper.toModelWithoutPasswordAndRoles(sampleUserRequestDto))
                .thenReturn(sampleUser);

        // When & Then: Attempting to register should throw a PasswordNotValidException
        assertThrows(PasswordNotValidException.class,
                () -> userService.register(sampleUserRequestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUserRequestDto.getEmail());
        verify(userMapper,times(1)).toModelWithoutPasswordAndRoles(sampleUserRequestDto);
        verifyNoMoreInteractions(userRepository, userMapper, roleTypeRepository);
    }

    @Test
    @DisplayName("Update user password with valid data")
    void updatePassword_updateUserPasswordWithValidData_UpdateUserPassword() {
        // Given: A sample user and a valid password update request
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserUpdatePasswordRequestDto requestDto = UserSampleUtil
                .createSampleUserUpdatePasswordRequestDto(sampleUser.getPassword());

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        sampleUser.setPassword(requestDto.getNewPassword());
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);

        // When: Updating the password
        userService.updatePassword(sampleUser.getEmail(), requestDto);

        // Then: The user repository should be called to find and save the user
        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verify(userRepository, times(1)).save(sampleUser);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update user password with valid data")
    void updatePassword_updateUserPasswordWithNotValidOldPassword_ThrowException() {
        // Given: A sample user and an update password request with an invalid old password
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserUpdatePasswordRequestDto requestDto = UserSampleUtil
                .createSampleUserUpdatePasswordRequestDto(sampleUser.getPassword() + "1234");

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(requestDto.getOldPassword(), sampleUser.getPassword()))
                .thenReturn(false);

        // When & Then: Expect an exception when trying to update
        // the password with an invalid old password
        assertThrows(PasswordNotValidException.class,
                () -> userService.updatePassword(sampleUser.getEmail(), requestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update user password with not equals new passwords")
    void updatePassword_updateUserPasswordWithNotValidNewPassword_ThrowException() {
        // Given: A sample user and an update password request where new passwords do not match
        User sampleUser = UserSampleUtil.createSampleUser(1L);
        UserUpdatePasswordRequestDto requestDto = UserSampleUtil
                .createSampleUserUpdatePasswordRequestDto(sampleUser.getPassword());
        requestDto.setRepeatNewPassword(requestDto.getNewPassword() + "1234");

        when(userRepository.findUserByEmail(sampleUser.getEmail()))
                .thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches(requestDto.getOldPassword(), sampleUser.getPassword()))
                .thenReturn(false);

        // When & Then: Expect an exception when new passwords do not match
        assertThrows(PasswordNotValidException.class,
                () -> userService.updatePassword(sampleUser.getEmail(), requestDto));

        verify(userRepository, times(1)).findUserByEmail(sampleUser.getEmail());
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    @DisplayName("Update user roles with valid data")
    void updateRoles_updateUserRolesWithValidData_ReturnUser() {
        // Given: A request to update user roles with valid role names
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

        // When: The user roles are updated
        UserResponseDto result = userService.updateRoles(sampleUser.getId(), requestDto);

        // Then: The response should not be null and should match the expected user response DTO
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
        // Given: A request to update user roles with an invalid user ID
        UserUpdateRolesRequestDto requestDto = new UserUpdateRolesRequestDto();
        requestDto.setRoleName(ADMIN_ROLE_TYPE);

        when(userRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then: Expect an EntityNotFoundException when the user ID is invalid
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
