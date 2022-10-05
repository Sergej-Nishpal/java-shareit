package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto addBooking(Long userId, BookingDto bookingDto) {
        userService.validateUserExists(userId);
        itemService.validateItemExists(bookingDto.getItemId());
        validateBookerIsOwner(userId, bookingDto.getItemId());
        bookingDto.setBookerId(userId);
        final Item item = itemRepository.getItemById(bookingDto.getItemId());
        final User booker = userRepository.getUserById(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        if (item.getAvailable() != null && item.getAvailable()) {
            booking.setStatus(BookingStatus.WAITING);
            bookingRepository.save(booking);
        } else {
            log.error("Вещь с id = {} недоступна для бронирования!", bookingDto.getItemId());
            throw new ItemUnavailableException("Вещь с id = " + bookingDto.getItemId() +
                    " недоступна для бронирования!");
        }

        log.debug("Вещь с id = {} сохранена в БД.", bookingDto.getItemId());
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDtoForResponse approveBooking(long userId, long bookingId, boolean approved) {
        userService.validateUserExists(userId);
        validateBookingExists(bookingId);
        final Booking booking = bookingRepository.getBookingById(bookingId);
        validateUserIsOwner(userId, booking.getItem().getId());
        validateBookingHaveSameStatus(booking, approved);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.debug("Бронирование> с id = {} подтверждено.", bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.debug("Бронирование> с id = {} отклонено.", bookingId);
        }

        return BookingMapper
                .toBookingDtoForResponse(booking);
    }

    private void validateBookingHaveSameStatus(Booking booking, boolean approved) {
        if (approved && booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("Бронирование уже имеет статус {}.", BookingStatus.APPROVED.name());
            throw new BookingStatusException("Бронирование уже имеет статус " + BookingStatus.APPROVED.name());
        }

        if (!approved && booking.getStatus().equals(BookingStatus.REJECTED)) {
            log.error("Бронирование уже имеет статус {}.", BookingStatus.REJECTED.name());
            throw new BookingStatusException("Бронирование уже имеет статус " + BookingStatus.REJECTED.name());
        }
    }

    @Override
    public BookingDtoForResponse getBookingInfo(long userId, long bookingId) {
        userService.validateUserExists(userId);
        validateBookingExists(bookingId);
        final Booking booking = bookingRepository.getBookingById(bookingId);
        long itemId = booking.getItem().getId();
        validateUserIsOwnerOrBooker(userId, itemId, booking);
        return BookingMapper.toBookingDtoForResponse(booking);
    }

    @Override
    public Collection<BookingDtoForResponse> getBookings(long userId, String state) {
        userService.validateUserExists(userId);
        final User booker = userRepository.getUserById(userId);
        Collection<Booking> bookings = getBookingsOfBooker(booker, state);
        return bookings.stream()
                .map(BookingMapper::toBookingDtoForResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDtoForResponse> getBookingsOfOwner(long userId, String state) {
        userService.validateUserExists(userId);
        final User owner = userRepository.getUserById(userId);
        Collection<Booking> bookings = getBookingsOfOwner(owner, state);
        return bookings.stream()
                .map(BookingMapper::toBookingDtoForResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void validateBookingExists(long id) {
        if (!bookingRepository.existsById(id)) {
            log.error("<Бронирование> с id = {} не найдено!", id);
            throw new ItemNotFoundException("Бронирование с id = " + id + " не найдено!");
        }
    }

    private void validateUserIsOwnerOrBooker(long userId, long itemId, Booking booking) {
        final Item item = itemRepository.getItemById(itemId);
        if (booking.getBooker().getId() != userId && item.getOwner().getId() != userId) {
            log.error("Пользователь с id = {} не является владельцем или автором бронирования " +
                    "вещи с id = {}.", userId, itemId);
            throw new BookingNotFoundException("Попытка выполнения операции " +
                    "посторонним пользователем с id = " + userId + "!");
        }
    }

    private void validateUserIsOwner(long userId, long itemId) {
        final Item item = itemRepository.getItemById(itemId);
        if (item.getOwner().getId() != userId) {
            log.error("Пользователь с id = {} не является владельцем " +
                    "вещи с id = {}.", userId, itemId);
            throw new ItemNotFoundException("Попытка выполнения операции " +
                    "посторонним пользователем с id = " + userId + "!");
        }
    }

    private void validateBookerIsOwner(long userId, long itemId) {
        final Item item = itemRepository.getItemById(itemId);
        if (item.getOwner().getId() == userId) {
            log.error("Пользователь с id = {} является владельцем " +
                    "вещи с id = {}.", userId, itemId);
            throw new InvalidBookingException("Попытка бронирования " +
                    "своей вещи владельцем с id = " + userId + "!");
        }
    }

    private Collection<Booking> getBookingsOfBooker(User booker, String state) {
        switch (state) {
            case "ALL":
                return bookingRepository.getAllByBookerOrderByStartDesc(booker);
            case "CURRENT":
                return bookingRepository.getAllCurrentByBookerOrderByStartDesc(booker);
            case "PAST":
                return bookingRepository.getAllPastByBookerOrderByStartDesc(booker);
            case "FUTURE":
                return bookingRepository.getAllFutureByBookerOrderByStartDesc(booker);
            case "WAITING":
                return bookingRepository.getAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.getAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED);
            default:
                log.error("Передан неизвестный статус бронирования: {}", state);
                throw new UnknownBookingStateException("Unknown state: " + state);
        }
    }

    private Collection<Booking> getBookingsOfOwner(User owner, String state) {
        switch (state) {
            case "ALL":
                return bookingRepository.getAllByItemOwnerOrderByStartDesc(owner);
            case "CURRENT":
                return bookingRepository.getAllCurrentByItemOwnerOrderByStartDesc(owner);
            case "PAST":
                return bookingRepository.getAllPastByItemOwnerOrderByStartDesc(owner);
            case "FUTURE":
                return bookingRepository.getAllFutureByItemOwnerOrderByStartDesc(owner);
            case "WAITING":
                return bookingRepository.getAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.WAITING);
            case "REJECTED":
                return bookingRepository.getAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED);
            default:
                log.error("Передан неизвестный статус бронирования: {}", state);
                throw new UnknownBookingStateException("Unknown state: " + state);
        }
    }
}