package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item add(long ownerId, Item item);

    Item update(long ownerId, long itemId, Item item);

    Item getById(long itemId);

    List<Item> getAll(long ownerId);

    List<Item> getByText(String text);
}