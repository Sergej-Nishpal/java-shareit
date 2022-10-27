package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.debug("Создание запроса пользователем с id = {}", userId);
        return itemRequestClient.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение всех запросов пользователя с id = {}", userId);
        return itemRequestClient.getAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsOfOther(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                        @PositiveOrZero @RequestParam(name = "from",
                                                                                defaultValue = "0") Integer from,
                                                                        @Positive @RequestParam(name = "size",
                                                                                defaultValue = "10") Integer size) {
        log.debug("Получение всех запросов пользователей, кроме id = {}", userId);
        return itemRequestClient.getAllRequestsOfOther(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable("requestId") Long requestId) {
        log.debug("Получение пользователем с id = {} запроса с id = {}", userId, requestId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}