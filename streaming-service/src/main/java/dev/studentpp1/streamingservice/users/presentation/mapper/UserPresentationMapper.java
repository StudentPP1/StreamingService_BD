package dev.studentpp1.streamingservice.users.presentation.mapper;

import dev.studentpp1.streamingservice.users.domain.model.User;
import dev.studentpp1.streamingservice.users.presentation.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPresentationMapper {
    UserDto toDto(User user);
}