package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedOperationException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        validateUserExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userRepository.getById(userId));
        final Item savedItem = itemRepository.add(userId, item);
        log.debug("Владелец с id = {} добавил новую вещь.}", userId);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        validateUserExists(userId);
        validateUsersItem(userId, itemId);
        log.debug("Владелец с id = {} обновляет вещь с id = {}", userId, itemId);
        final Item item = itemRepository.update(userId, itemId, ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getById(long userId, long itemId) {
        log.debug("Пользователь с id = {} запрашивает информацию о вещи с id = {}.", userId, itemId);
        Item item = itemRepository.getById(itemId);
        if (item == null) {
            log.error("Вещь с id = {} не найдена!", itemId);
            throw new ItemNotFoundException("Вещь с id = " + itemId + " не найдена!");
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Collection<ItemDto> getAll(long userId) {
        log.debug("Владелец с id = {} запросил список своих вещей.", userId);
        return itemRepository.getAll(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getByText(long userId, String text) {
        if (text.isEmpty()) {
            log.debug("Текст для поиска не содержит символов!");
            return Collections.emptyList();
        } else {
            log.debug("Ищем вещи по поисковому запросу \"{}\".", text);
            return itemRepository.getByText(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    private void validateUserExists(long id) {
        if (userRepository.getById(id) == null) {
            log.error("Пользователь с id = {} не найден в БД.", id);
            throw new UserNotFoundException("Пользователь с id = " + id + " не найден!");
        }
    }

    private void validateUsersItem(long userId, long itemId) {
        if (itemRepository.getAll(userId) == null
                || itemRepository.getAll(userId)
                .stream()
                .map(Item::getId)
                .noneMatch(id -> id == itemId)) {
            log.error("Пользователь с id = {} не является владельцем запрашиваемой вещи с id = {}.", userId, itemId);
            throw new UnauthorizedOperationException("Попытка выполнения операции " +
                    "посторонним пользователем с id = " + userId + "!");
        }
    }
}