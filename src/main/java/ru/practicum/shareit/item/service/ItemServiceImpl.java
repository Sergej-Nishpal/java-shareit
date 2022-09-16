package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UnauthorizedOperationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        validateUserExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getUserById(userId));
        final Item savedItem = itemRepository.addItem(userId, item);
        log.debug("Владелец с id = {} добавил новую вещь.}", userId);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        validateUserExists(userId);
        validateUsersItem(userId, itemId);
        log.debug("Владелец с id = {} обновляет вещь с id = {}", userId, itemId);
        final Item item = itemRepository.updateItem(userId, itemId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        log.debug("Пользователь с id = {} запрашивает информацию о вещи с id = {}.", userId, itemId);
        Item item = itemRepository.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        log.debug("Владелец с id = {} запросил список своих вещей.", userId);
        return itemRepository.getItems(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsByText(long userId, String text) {
        if (text.isEmpty()) {
            log.debug("Текст для поиска не содержит символов!");
            return Collections.emptyList();
        } else {
            log.debug("Ищем вещи по поисковому запросу \"{}\".", text);
            return itemRepository.getItemsByText(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    private void validateUserExists(long id) {
        if (userRepository.getUserById(id) == null) {
            log.error("Пользователь с id = {} не найден в БД.", id);
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден!");
        }
    }

    private void validateUsersItem(long userId, long itemId) {
        if (itemRepository.getItems(userId) == null
                || itemRepository.getItems(userId)
                .stream()
                .map(Item::getId)
                .noneMatch(id -> id == itemId)) {
            log.error("Пользователь с id = {} не является владельцем запрашиваемой вещи с id = {}.", userId, itemId);
            throw new UnauthorizedOperationException("Попытка выполнения операции " +
                    "посторонним пользователем с id = " + userId + "!");
        }
    }
}
