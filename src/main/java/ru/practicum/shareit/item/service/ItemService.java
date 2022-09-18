package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto add(Long userId, ItemDto itemDto);

    ItemDto update(long userId, long itemId, ItemDto itemDto);

    ItemDto getById(long userId, long itemId);

    Collection<ItemDto> getAll(long userId);

    Collection<ItemDto> getByText(long userId, String text);
}