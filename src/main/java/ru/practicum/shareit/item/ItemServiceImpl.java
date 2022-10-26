package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.IncorrectCommentException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnauthorizedOperationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    @Override
    @Transactional
    public ItemDto add(Long userId, ItemDto itemDto) {
        final User owner = userService.getUser(userId);
        ItemRequest itemRequest = null;
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestService.getItemRequestById(itemDto.getRequestId());
        }

        final Item item = ItemMapper.toItem(itemDto, owner, itemRequest);
        final Item savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        userService.validateUserExists(userId);
        validateIsUsersItem(userId, itemId);
        Item savedItem = getItem(itemId);

        if (itemDto.getName() != null) {
            savedItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            savedItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(savedItem.getAvailable())) {
            savedItem.setAvailable(itemDto.getAvailable());
        }

        final Item item = itemRepository.save(savedItem);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDtoForResponse getById(long userId, long itemId) {
        userService.validateUserExists(userId);
        final Item item = getItem(itemId);
        if (item.getOwner().getId() == userId) {
            return ItemMapper.toItemDtoForResponse(item,
                    bookingRepository.getCurrentOrPastBookingByItem(item),
                    bookingRepository.getFutureBookingByItem(item));
        } else {
            return ItemMapper.toItemDtoForResponse(item, null, null);
        }
    }

    @Override
    public Collection<ItemDtoForResponse> getAll(long userId) {
        userService.validateUserExists(userId);
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> ItemMapper.toItemDtoForResponse(item,
                        bookingRepository.getCurrentOrPastBookingByItem(item),
                        bookingRepository.getFutureBookingByItem(item)))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> getByText(long userId, String text) {
        userService.validateUserExists(userId);
        if (text.isEmpty()) {
            log.debug("Текст для поиска не содержит символов!");
            return Collections.emptyList();
        } else {
            return itemRepository.search(text)
                    .stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public CommentDtoForResponse addComment(CommentDto commentDto, Long itemId, Long userId) {
        final Item item = getItem(itemId);
        final User user = userService.getUser(userId);
        validateItemWasBookedByUser(item, user);
        final Comment comment = CommentMapper.toComment(commentDto, item, user);
        return CommentMapper.toCommentDtoForResponse(commentRepository.save(comment));
    }

    @Override
    public Item getItem(long id) {
        return itemRepository.findById(id).orElseThrow(() -> {
            log.error("Вещь с id = {} не найдена!", id);
            throw new ItemNotFoundException(id);
        });
    }

    @Override
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

    private void validateItemWasBookedByUser(Item item, User user) {
        if (bookingRepository.getAllPastByBookerOrderByStartDesc(user, PageRequest.of(0, 1))
                .stream()
                .map(Booking::getItem)
                .noneMatch(item::equals)) {
            log.error("Пользователь с id = {} не арендовал ранее вещь с id = {}.", user.getId(), item.getId());
            throw new IncorrectCommentException("Попытка добавить комментарий " +
                    "к незнакомой вещи пользователем с id = " + user.getId() + "!");
        }
    }
}