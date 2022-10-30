package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        log.debug("Запрос списка всех пользователей.");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") Long userId) {
        log.debug("Запрос пользователя с id = {}.", userId);
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.debug("Создание нового пользователя.");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") Long userId,
                          @RequestBody UserDto userDto) {
        log.debug("Запрос обновления пользователя с id = {}.", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long userId) {
        log.debug("Удаление пользователя с id = {}.", userId);
        userService.deleteById(userId);
    }
}