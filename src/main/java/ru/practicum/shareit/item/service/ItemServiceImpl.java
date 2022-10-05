package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedOperationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto add(Long userId, ItemDto itemDto) {
        userService.validateUserExists(userId);
        final User owner = userRepository.getUserById(userId);
        final Item item = ItemMapper.toItem(itemDto, owner);

        final Item savedItem = itemRepository.save(item);
        log.debug("Владелец с id = {} добавил новую вещь с id = {}.", userId, savedItem.getId());
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        userService.validateUserExists(userId);
        validateIsUsersItem(userId, itemId);

        Item savedItem = itemRepository.getItemById(itemId);

        if (itemDto.getName() != null) {
            savedItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            savedItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(savedItem.getAvailable())) {
            savedItem.setAvailable(itemDto.getAvailable());
        }

        log.debug("Владелец с id = {} обновляет вещь с id = {}", userId, itemId);
        final Item item = itemRepository.save(savedItem);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDtoForResponse getById(long userId, long itemId) {
        userService.validateUserExists(userId);
        if (!itemRepository.existsById(itemId)) {
            log.error("Вещь с id = {} не найдена!", itemId);
            throw new ItemNotFoundException("Вещь с id = " + itemId + " не найдена!");
        }

        log.debug("Пользователь с id = {} запрашивает информацию о вещи с id = {}.", userId, itemId);
        final Item item = itemRepository.getItemById(itemId);
        if (item.getOwner().getId() == userId) {
            return ItemMapper.toItemDtoForResponse(item,
                    bookingRepository.getCurrentOrPastBookingByItem(item),
                    bookingRepository.getFutureBookingByItem(item));
        } else {
            return ItemMapper.toItemDtoForResponse(item, null, null);
        }
    }

    @Override
    @Transactional
    public Collection<ItemDtoForResponse> getAll(long userId) {
        userService.validateUserExists(userId);
        log.debug("Владелец с id = {} запросил список своих вещей.", userId);
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> ItemMapper.toItemDtoForResponse(item,
                        bookingRepository.getCurrentOrPastBookingByItem(item),
                        bookingRepository.getFutureBookingByItem(item)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Collection<ItemDto> getByText(long userId, String text) {
        userService.validateUserExists(userId);
        if (text.isEmpty()) {
            log.debug("Текст для поиска не содержит символов!");
            return Collections.emptyList();
        } else {
            log.debug("Ищем вещи по поисковому запросу \"{}\".", text);
            return itemRepository.search(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void validateItemExists(long itemId) {
        if (!itemRepository.existsById(itemId)) {
            log.error("Передан несуществующий id вещи!");
            throw new ItemNotFoundException("Вещь с id = " + itemId + " не найдена!");
        }
    }

    @Transactional
    public void validateIsUsersItem(long userId, long itemId) {
        if (itemRepository.findAllByOwnerIdOrderByIdAsc(userId) == null
                || itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(Item::getId)
                .noneMatch(id -> id == itemId)) {
            log.error("Пользователь с id = {} не является владельцем запрашиваемой вещи с id = {}.", userId, itemId);
            throw new UnauthorizedOperationException("Попытка выполнения операции " +
                    "посторонним пользователем с id = " + userId + "!");
        }
    }
}