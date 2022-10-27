package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAll();

    UserDto getById(Long userId);

    User getUser(long id);

    UserDto create(UserDto user);

    UserDto update(Long userId, UserDto userDto);

    void deleteById(Long userId);

    void validateUserExists(Long id);
}