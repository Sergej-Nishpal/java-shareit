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
    public Collection<UserDto> findAll() {
        log.debug("Запросили всех пользователей из БД.");
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            log.error("Запросили всех пользователей из БД.");
            throw new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
        }

        log.debug("Запросили пользователя с id = {}.", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (emailExists(userDto.getEmail())) {
            log.error("Передан существующий email!");
            throw new SuchEmailExistsException("Указанный email уже существует!");
        }

        User user = userRepository.create(UserMapper.toUser(userDto));
        log.debug("Создали пользователя с id = {}.", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        if (userIdNotExists(userId)) {
            log.error("Передан несуществующий id пользователя!");
            throw new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
        }

        if (userDto.getEmail() != null && emailExists(userDto.getEmail())) {
            log.error("Передан существующий email!");
            throw new SuchEmailExistsException("Указанный email уже существует!");
        }

        User user = userRepository.update(userId, UserMapper.toUser(userDto));
        log.debug("Обновили пользователя с id = {}.", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public void deleteById(Long userId) {
        if (userIdNotExists(userId)) {
            log.error("Передан несуществующий id пользователя!");
            throw new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
        }

        userRepository.deleteById(userId);
        log.debug("Удалили пользователя с id = {}.", userId);
    }

    private boolean emailExists(String email) {
        Collection<User> users = userRepository.findAll();
        return users.stream()
                .map(User::getEmail)
                .anyMatch(s -> Objects.equals(s, email));
    }

    private boolean userIdNotExists(Long userId) {
        Collection<User> users = userRepository.findAll();
        return users.stream()
                .map(User::getId)
                .noneMatch(s -> Objects.equals(s, userId));
    }
}