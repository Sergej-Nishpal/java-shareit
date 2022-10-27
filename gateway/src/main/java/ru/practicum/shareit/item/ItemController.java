package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestBody @Valid ItemDto itemDto) {
        log.debug("Пользователь с id = {} добавляет новую вещь.", userId);
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    @Validated({Marker.OnUpdate.class})
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                          @PathVariable long itemId,
                          @RequestBody @Valid ItemDto itemDto) {
        log.debug("Пользователь с id = {} обновляет вещь.", userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId) {
        log.debug("Пользователь с id = {} запрашивает вещь с id = {}.", userId, itemId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.debug("Пользователь с id = {} запрашивает список своих вещей.", userId);
        return itemClient.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getByText(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam(name = "text") String text) {
        log.debug("Пользователь с id = {} ищет вещь по запросу \"{}\".", userId, text);
        return itemClient.getByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") Long itemId,
                                            @Valid @RequestBody CommentDto commentCreateDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Пользователь с id = {} добавляет комментарий к вещи с id = {}.", userId, itemId);
        return itemClient.addComment(commentCreateDto, itemId, userId);
    }
}