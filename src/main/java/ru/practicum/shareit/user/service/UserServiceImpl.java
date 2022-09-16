package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.SuchEmailExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAllUsers() {
        return userRepository
                .findAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        if (userId <= 0) {
            log.error("Передан некорректный id пользователя!");
            throw new ValidationException("id должен быть больше нуля!");
        }

        User user = userRepository.getUserById(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (emailExists(userDto.getEmail())) {
            log.error("Передан существующий email!");
            throw new SuchEmailExistsException("Указанный email уже существует!");
        }

        User user = userRepository.createUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (userId <= 0) {
            log.error("Передан некорректный id пользователя!");
            throw new ValidationException("id должен быть больше нуля!");
        }

        if (userIdNotExists(userId)) {
            log.error("Передан несуществующий id пользователя!");
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден!");
        }

        if (userDto.getEmail() != null && emailExists(userDto.getEmail())) {
            log.error("Передан существующий email!");
            throw new SuchEmailExistsException("Указанный email уже существует!");
        }

        User user = userRepository.updateUser(userId, UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        if (userIdNotExists(userId)) {
            log.error("Передан несуществующий id пользователя!");
            throw new UserNotFoundException("Пользователь с id = " + userId + " не найден!");
        }

        userRepository.deleteUserById(userId);
    }

    private boolean emailExists(String email) {
        Collection<User> users = userRepository.findAllUsers();
        return users.stream()
                .map(User::getEmail)
                .anyMatch(s -> Objects.equals(s, email));
    }

    private boolean userIdNotExists(Long userId) {
        Collection<User> users = userRepository.findAllUsers();
        return users.stream()
                .map(User::getId)
                .noneMatch(s -> Objects.equals(s, userId));
    }
}
