package dev.studentpp1.streamingservice.users.mapper;

import dev.studentpp1.streamingservice.users.dto.UpdateUserRequest;
import dev.studentpp1.streamingservice.users.entity.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        // create spring bean for injecting
        componentModel = "spring",
        // ignore null values in UpdateUserRequest
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UpdateUserMapper {

    // update fields in target (if not null)
    void updateFromDto(UpdateUserRequest dto, @MappingTarget AppUser entity);
}

