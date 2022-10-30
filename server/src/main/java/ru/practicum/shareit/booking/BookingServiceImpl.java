package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDtoForResponse addBooking(Long userId, BookingDto bookingDto) {
        final User booker = userService.getUser(userId);
        final Item item = itemService.getItem(bookingDto.getItemId());
        validateBookerIsOwner(booker, item);
        bookingDto.setBookerId(userId);
        final Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        if (item.getAvailable() != null && item.getAvailable()) {
            booking.setStatus(BookingStatus.WAITING);
            bookingRepository.save(booking);
        } else {
            log.error("Вещь с id = {} недоступна для бронирования!", bookingDto.getItemId());
            throw new ItemUnavailableException(bookingDto.getItemId());
        }

        return BookingMapper.toBookingDtoForResponse(booking);
    }

    @Override
    @Transactional
    public BookingDtoForResponse approveBooking(long userId, long bookingId, boolean approved) {
        final User user = userService.getUser(userId);
        final Booking booking = getBooking(bookingId);
        validateUserIsOwner(user, booking.getItem());
        validateBookingHaveSameStatus(booking, approved);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.debug("Бронирование с id = {} подтверждено.", bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.debug("Бронирование с id = {} отклонено.", bookingId);
        }

        return BookingMapper
                .toBookingDtoForResponse(booking);
    }

    @Override
    public BookingDtoForResponse getBookingInfo(long userId, long bookingId) {
        final User user = userService.getUser(userId);
        final Booking booking = getBooking(bookingId);
        final Item item = itemService.getItem(booking.getItem().getId());
        validateUserIsOwnerOrBooker(user, booking, item);
        return BookingMapper.toBookingDtoForResponse(booking);
    }

    @Override
    public Collection<BookingDtoForResponse> getBookings(long userId, String state, int from, int size) {
        final User booker = userService.getUser(userId);
        final Pageable pageable = PageRequest.of(from / size, size);
        final Page<Booking> bookings = getBookingsOfBooker(booker, state, pageable);
        return bookings.stream()
                .map(BookingMapper::toBookingDtoForResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDtoForResponse> getBookingsOfOwner(long userId, String state, int from, int size) {
        final User owner = userService.getUser(userId);
        final Pageable pageable = PageRequest.of(from / size, size);
        final Page<Booking> bookings = getBookingsOfOwner(owner, state, pageable);
        return bookings.stream()
                .map(BookingMapper::toBookingDtoForResponse)
                .collect(Collectors.toList());
    }

    private Booking getBooking(long id) {
        return bookingRepository.findById(id).orElseThrow(() -> {
            log.error("Бронирование с id = {} не найдено!", id);
            throw new BookingNotFoundException(id);
        });
    }

    private void validateUserIsOwnerOrBooker(User user, Booking booking, Item item) {
        if (!booking.getBooker().getId().equals(user.getId()) && !item.getOwner().getId().equals(user.getId())) {
            log.error("Пользователь с id = {} не является владельцем или автором бронирования " +
                    "вещи с id = {}.", user.getId(), item.getId());
            throw new BookingNotFoundException(booking.getId());
        }
    }

    private void validateUserIsOwner(User user, Item item) {
        if (!item.getOwner().getId().equals(user.getId())) {
            log.error("Попытка выполнения операции {} посторонним пользователем с id = ", user.getId());
            throw new ItemNotFoundException(user.getId());
        }
    }

    private void validateBookerIsOwner(User booker, Item item) {
        if (item.getOwner().getId().equals(booker.getId())) {
            log.error("Попытка бронирования своей вещи владельцем с id = {}.", booker.getId());
            throw new InvalidBookingException("Попытка бронирования " +
                    "своей вещи владельцем с id = " + booker.getId() + "!");
        }
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

    private Page<Booking> getBookingsOfBooker(User booker, String state, Pageable pageable) {
        if ("ALL".equals(state)) {
            return bookingRepository.getAllByBookerOrderByStartDesc(booker, pageable);
        } else if ("CURRENT".equals(state)) {
            return bookingRepository.getAllCurrentByBookerOrderByStartDesc(booker, pageable);
        } else if ("PAST".equals(state)) {
            return bookingRepository.getAllPastByBookerOrderByStartDesc(booker, pageable);
        } else if ("FUTURE".equals(state)) {
            return bookingRepository.getAllFutureByBookerOrderByStartDesc(booker, pageable);
        } else if ("WAITING".equals(state)) {
            return bookingRepository.getAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.WAITING,
                    pageable);
        } else if ("REJECTED".equals(state)) {
            return bookingRepository.getAllByBookerAndStatusOrderByStartDesc(booker, BookingStatus.REJECTED,
                    pageable);
        } else {
            return Page.empty();
        }
    }

    private Page<Booking> getBookingsOfOwner(User owner, String state, Pageable pageable) {
        if ("ALL".equals(state)) {
            return bookingRepository.getAllByItemOwnerOrderByStartDesc(owner, pageable);
        } else if ("CURRENT".equals(state)) {
            return bookingRepository.getAllCurrentByItemOwnerOrderByStartDesc(owner, pageable);
        } else if ("PAST".equals(state)) {
            return bookingRepository.getAllPastByItemOwnerOrderByStartDesc(owner, pageable);
        } else if ("FUTURE".equals(state)) {
            return bookingRepository.getAllFutureByItemOwnerOrderByStartDesc(owner, pageable);
        } else if ("WAITING".equals(state)) {
            return bookingRepository.getAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.WAITING,
                    pageable);
        } else if ("REJECTED".equals(state)) {
            return bookingRepository.getAllByItemOwnerAndStatusOrderByStartDesc(owner, BookingStatus.REJECTED,
                    pageable);
        } else {
            return Page.empty();
        }
    }
}