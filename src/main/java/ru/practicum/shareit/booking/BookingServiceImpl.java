package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UnauthorizedOperationException;
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

        bookingDto.setBookerId(userId);
        Booking booking = BookingMapper.toBooking(bookingDto);
        Item item = itemRepository.getItemById(bookingDto.getItemId());

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
    public BookingGetDto approveBooking(long userId, long bookingId, boolean approved) {
        userService.validateUserExists(userId);
        validateBookingExists(bookingId);

        Booking booking = bookingRepository.getBookingById(bookingId);
        itemService.validateIsUsersItem(userId, booking.getItemId());

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
            log.debug("Бронирование> с id = {} подтверждено.", bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            log.debug("Бронирование> с id = {} отклонено.", bookingId);
        }
        return BookingMapper
                .toBookingGetDto(booking,
                        userRepository.getUserById(booking.getBookerId()),
                        itemRepository.getItemById(booking.getItemId()));
    }

    @Override
    public BookingGetDto getBookingInfo(long userId, long bookingId) {
        userService.validateUserExists(userId);
        validateBookingExists(bookingId);

        Booking booking = bookingRepository.getBookingById(bookingId);
        long itemId = booking.getItemId();

        if (userId == itemRepository.getItemById(itemId).getOwnerId() || userId == booking.getBookerId()) {
            final User user = userRepository.getUserById(userId);
            final Item item = itemRepository.getItemById(itemId);
            return BookingMapper
                    .toBookingGetDto(booking, user, item);
        } else {
            log.error("Пользователь с id = {} не является владельцем или автором бронирования " +
                    "вещи с id = {}.", userId, itemId);
            throw new UnauthorizedOperationException("Попытка выполнения операции " +
                    "посторонним пользователем с id = " + userId + "!");
        }
    }

    @Override
    public Collection<BookingGetDto> getBookings(long userId, BookingStatus state) {
        userService.validateUserExists(userId);
        Collection<Booking> bookings = bookingRepository
                .getBookingsByBookerIdAndStatusOrderByBookerIdAsc(userId, state);
        return bookings.stream()
                .map(booking -> BookingMapper
                        .toBookingGetDto(booking,
                                userRepository.getUserById(booking.getBookerId()),
                                itemRepository.getItemById(booking.getItemId())))
                .collect(Collectors.toList());
    }

    @Override
    public void validateBookingExists(long id) {
        if (!bookingRepository.existsById(id)) {
            log.error("<Бронирование> с id = {} не найдено!", id);
            throw new ItemNotFoundException("Бронирование с id = " + id + " не найдено!");
        }
    }
}