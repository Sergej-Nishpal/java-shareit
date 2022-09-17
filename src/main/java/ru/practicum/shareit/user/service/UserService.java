package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAllUsers();

    UserDto getUserById(Long userId);

    UserDto createUser(UserDto user);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUserById(Long userId);
}