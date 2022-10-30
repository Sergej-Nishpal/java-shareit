package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoForResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                       @RequestBody ItemDto itemDto) {
        log.debug("Пользователь с id = {} добавляет новую вещь.", userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody ItemDto itemDto) {
        log.debug("Пользователь с id = {} обновляет вещь.", userId);
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForResponse getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId) {
        log.debug("Пользователь с id = {} запрашивает вещь с id = {}.", userId, itemId);
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public Collection<ItemDtoForResponse> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Пользователь с id = {} запрашивает список своих вещей.", userId);
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "text") String text) {
        log.debug("Пользователь с id = {} ищет вещь по запросу \"{}\".", userId, text);
        return itemService.getByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoForResponse addComment(@PathVariable("itemId") Long itemId,
                                            @RequestBody CommentDto commentCreateDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Пользователь с id = {} добавляет комментарий к вещи с id = {}.", userId, itemId);
        return itemService.addComment(commentCreateDto, itemId, userId);
    }
}