package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoForResponse addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Создание запроса пользователем с id = {}", userId);
        return itemRequestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public Collection<ItemRequestDtoForResponse> getItemRequestsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получение всех запросов пользователя с id = {}", userId);
        return itemRequestService.getAllRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDtoForResponse> getItemRequestsOfOther(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                        @RequestParam(name = "from",
                                                                                defaultValue = "0") Integer from,
                                                                        @RequestParam(name = "size",
                                                                                defaultValue = "10") Integer size) {
        log.debug("Получение всех запросов пользователей, кроме id = {}", userId);
        return itemRequestService.getAllRequestsOfOther(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoForResponse getItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @PathVariable("requestId") Long requestId) {
        log.debug("Получение пользователем с id = {} запроса с id = {}", userId, requestId);
        return itemRequestService.getItemRequest(userId, requestId);
    }
}