package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static final String EMPTY_STRING = "";

    private UserMapper() {

    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName() != null ? userDto.getName() : EMPTY_STRING)
                .email(userDto.getEmail() != null ? userDto.getEmail() : EMPTY_STRING)
                .build();
    }
}
