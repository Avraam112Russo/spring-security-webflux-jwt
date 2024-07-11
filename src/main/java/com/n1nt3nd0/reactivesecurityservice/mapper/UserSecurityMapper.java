package com.n1nt3nd0.reactivesecurityservice.mapper;

import com.n1nt3nd0.reactivesecurityservice.dto.UserSecurityDto;
import com.n1nt3nd0.reactivesecurityservice.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSecurityMapper {
    @InheritInverseConfiguration
    UserEntity toUserSecurity(UserSecurityDto dto);
    UserSecurityDto toUserSecurityDto(UserEntity userEntity);
}
