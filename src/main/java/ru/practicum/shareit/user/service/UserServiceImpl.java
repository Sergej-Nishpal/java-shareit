package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.SuchEmailExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
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
    private static final String USER_WITH_ID = "Пользователь с id = ";
    private static final String NOT_FOUND = " не найден!";

    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAllUsers() {
        log.debug("Запросили всех пользователей из БД.");
        return userRepository
                .findAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            log.error("Запросили всех пользователей из БД.");
            throw new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
        }

        log.debug("Запросили пользователя с id = {}.", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (emailExists(userDto.getEmail())) {
            log.error("Передан существующий email!");
            throw new SuchEmailExistsException("Указанный email уже существует!");
        }

        User user = userRepository.createUser(UserMapper.toUser(userDto));
        log.debug("Создали пользователя с id = {}.", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (userIdNotExists(userId)) {
            log.error("Передан несуществующий id пользователя!");
            throw new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
        }

        if (userDto.getEmail() != null && emailExists(userDto.getEmail())) {
            log.error("Передан существующий email!");
            throw new SuchEmailExistsException("Указанный email уже существует!");
        }

        User user = userRepository.updateUser(userId, UserMapper.toUser(userDto));
        log.debug("Обновили пользователя с id = {}.", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        if (userIdNotExists(userId)) {
            log.error("Передан несуществующий id пользователя!");
            throw new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
        }

        userRepository.deleteUserById(userId);
        log.debug("Удалили пользователя с id = {}.", userId);
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