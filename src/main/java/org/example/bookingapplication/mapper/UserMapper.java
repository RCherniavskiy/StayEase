package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.users.request.UserRequestDto;
import org.example.bookingapplication.dto.users.request.UserUpdateInfoRequestDto;
import org.example.bookingapplication.dto.users.response.UserResponseDto;
import org.example.bookingapplication.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class, uses = RoleTypeMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    UserResponseDto toResponseDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toModelWithoutPasswordAndRoles(UserRequestDto requestDto);

    void setUpdateInfoToUser(@MappingTarget User user, UserUpdateInfoRequestDto requestDto);
}
