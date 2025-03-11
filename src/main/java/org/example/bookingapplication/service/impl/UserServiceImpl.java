package org.example.bookingapplication.service.impl;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
import org.example.bookingapplication.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private static final RoleType.RoleName CUSTOMER_ROLE_TYPE = RoleType.RoleName.CUSTOMER;
    private final RoleTypeRepository roleTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserResponseDto getInfo(String email) {
        return userMapper.toResponseDto(getUser(email));
    }

    @Override
    public UserResponseDto updateInfo(String email, UserUpdateInfoRequestDto requestDto) {
        User user = getUser(email);
        setUserInfo(user, requestDto);
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    public void updatePassword(String email, UserUpdatePasswordRequestDto requestDto) {
        User user = getUser(email);
        if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
            throw new PasswordNotValidException("Old password don't valid");
        }
        isPasswordsValid(requestDto.getNewPassword(), requestDto.getRepeatNewPassword());
        setPassword(user, requestDto.getNewPassword());
        userRepository.save(user);
    }

    @Override
    public UserResponseDto updateRoles(Long id, UserUpdateRolesRequestDto requestDto) {
        User user = getUser(id);
        setRoleType(user, requestDto.getRoleName());
        return userMapper.toResponseDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto register(UserRequestDto requestDto) {
        isUserAlreadyExist(requestDto.getEmail());
        User newUser = userMapper.toModelWithoutPasswordAndRoles(requestDto);
        isPasswordsValid(requestDto.getPassword(), requestDto.getRepeatPassword());
        setPassword(newUser, requestDto.getPassword());
        setRoleType(newUser, CUSTOMER_ROLE_TYPE);
        return userMapper.toResponseDto(userRepository.save(newUser));
    }

    private void setUserInfo(User user, UserUpdateInfoRequestDto requestDto) {
        isUserAlreadyExist(requestDto.getEmail());
        userMapper.setUpdateInfoToUser(user, requestDto);
    }

    private void isUserAlreadyExist(String email) {
        Optional<User> userByEmail = userRepository.findUserByEmail(email);
        if (userByEmail.isPresent()) {
            throw new EntityAlreadyExistsException("User with email: " + email + " already exists");
        }
    }

    private User getUser(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with email: " + email));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user with id: " + id));
    }

    private void setPassword(User user, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);
    }

    private void isPasswordsValid(String password, String repeatPassword) {
        if (!password.equals(repeatPassword)) {
            throw new PasswordNotValidException("Passwords are different");
        }
    }

    private void setRoleType(User user, RoleType.RoleName highestRole) {
        List<RoleType.RoleName> roleNamesSubList = RoleType.RoleName.getRolesUpTo(highestRole);
        List<RoleType> roleTypes = roleTypeRepository.findRoleTypesByNameIn(roleNamesSubList);
        Set<RoleType> newRoleTypes = new HashSet<>(roleTypes);
        user.setRoles(newRoleTypes);
    }
}
