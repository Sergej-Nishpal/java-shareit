package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        return userRepository
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long userId) {
        final User user = getUser(userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        final User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User savedUser = getUser(userId);

        if (userDto.getName() != null) {
            savedUser.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            savedUser.setEmail(userDto.getEmail());
        }

        userRepository.save(savedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> {
            log.error("Пользователь с id = {} не найден!", id);
            return new UserNotFoundException(id);
        });
    }

    @Override
    public void validateUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            log.error("Передан id несуществующего пользователя: {}.", id);
            throw new UserNotFoundException(id);
        }
    }
}