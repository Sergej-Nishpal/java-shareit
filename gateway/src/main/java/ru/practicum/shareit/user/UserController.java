package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.debug("Запрос списка всех пользователей.");
        return userClient.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long userId) {
        log.debug("Запрос пользователя с id = {}.", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.debug("Создание нового пользователя.");
        return userClient.create(userDto);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Object> update(@PathVariable("id") Long userId,
                          @RequestBody @Valid UserDto userDto) {
        log.debug("Запрос обновления пользователя с id = {}.", userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long userId) {
        log.debug("Удаление пользователя с id = {}.", userId);
        userClient.deleteById(userId);
    }
}