package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoForResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    ItemDto add(Long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDtoForResponse getById(long userId, long itemId);

    Item getItem(long id);

    Collection<ItemDtoForResponse> getAll(long userId);

    Collection<ItemDto> getByText(long userId, String text);

    void validateIsUsersItem(long userId, long itemId);

    CommentDtoForResponse addComment(CommentDto commentCreateDto, Long itemId, Long userId);
}