package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@Validated
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
    @Validated({Marker.OnCreate.class})
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.debug("Создание нового пользователя.");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public UserDto update(@PathVariable("id") Long userId,
                          @RequestBody @Valid UserDto userDto) {
        log.debug("Запрос обновления пользователя с id = {}.", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long userId) {
        log.debug("Удаление пользователя с id = {}.", userId);
        userService.deleteById(userId);
    }
}