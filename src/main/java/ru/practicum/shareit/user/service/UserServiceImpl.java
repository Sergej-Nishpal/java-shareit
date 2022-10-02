package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.NoSuchElementException;
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
        log.debug("Запросили пользователя с id = {}.", userId);
        UserDto userDto;
        try {
            userDto = UserMapper.toUserDto(userRepository.findById(userId).orElseThrow());
        } catch (NoSuchElementException e) {
            log.error("Пользователь с id = {} не найден!", userId);
            throw new UserNotFoundException(USER_WITH_ID + userId + NOT_FOUND);
        }
        return userDto;
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.debug("Создали пользователя с id = {}.", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        validateUserExists(userId);
        User savedUser = userRepository.getUserById(userId);

        if (userDto.getName() != null) {
            savedUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            savedUser.setEmail(userDto.getEmail());
        }

        userRepository.save(savedUser);
        log.debug("Обновили пользователя с id = {}.", savedUser.getId());
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteById(Long userId) {
        validateUserExists(userId);
        userRepository.deleteById(userId);
        log.debug("Удалили пользователя с id = {}.", userId);
    }

    @Override
    public void validateUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            log.error("Передан несуществующий id пользователя!");
            throw new UserNotFoundException(USER_WITH_ID + id + NOT_FOUND);
        }
    }
}