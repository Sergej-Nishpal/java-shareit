package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(long ownerId, Item item);

    Item updateItem(long ownerId, long itemId, Item item);

    Item getItemById(long itemId);

    List<Item> getItems(long ownerId);

    List<Item> getItemsByText(String text);
}