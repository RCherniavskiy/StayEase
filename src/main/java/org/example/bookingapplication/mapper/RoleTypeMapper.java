package org.example.bookingapplication.mapper;

import org.example.bookingapplication.config.MapperConfig;
import org.example.bookingapplication.dto.roletypes.response.RoleTypeDto;
import org.example.bookingapplication.model.user.RoleType;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface RoleTypeMapper {
    RoleTypeDto toDto(RoleType roleType);
}
