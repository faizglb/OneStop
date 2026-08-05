package com.acea.backend.mapper;

import com.acea.backend.domain.entity.User;
import com.acea.backend.dto.response.UserResponse;
import org.mapstruct.*;

/**
 * MapStruct mapper for {@link User} ↔ DTO conversions.
 *
 * <p>MapStruct generates the implementation at compile-time — zero reflection overhead.
 * Uses componentModel = "spring" to register as a Spring bean.
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserMapper {

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserResponse toResponse(User user);
}
