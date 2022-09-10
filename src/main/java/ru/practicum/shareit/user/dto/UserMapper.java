package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {

    private UserMapper() {

    }

    public static UserDto toUserDto(User user) {
        return new UserDto.UserDtoBuilder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
