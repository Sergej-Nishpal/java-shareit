package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long itemRepoIdCounter;

    @Override
    public Item add(long ownerId, Item item) {
        item.setId(++itemRepoIdCounter);
        items.compute(ownerId, (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public Item update(long ownerId, long itemId, Item item) {
        if (item.getId() == null) {
            item.setId(itemId);
        }

        Item updatedItem = getById(item.getId());
        if (item.getName() != null) {
            updatedItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            updatedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null && !item.getAvailable().equals(updatedItem.getAvailable())) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    public Item getById(long itemId) {
        return items.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
    }

    public List<Item> getAll(long ownerId) {
        return items.get(ownerId);
    }

    @Override
    public List<Item> getByText(String text) {
        return items.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(item -> checkContainsText(item, text) && item.getAvailable())
                .collect(Collectors.toList());
    }

    private boolean checkContainsText(final Item item, final String text) {
        return item.getName().toLowerCase().contains(text.toLowerCase())
                || item.getDescription().toLowerCase().contains(text.toLowerCase());
    }
}