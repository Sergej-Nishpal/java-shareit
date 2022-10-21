package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingRepository bookingRepository;

    User owner;
    Item item;
    User booker;
    Booking booking;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(userService, itemService, bookingRepository);

        owner = User.builder()
                .id(1L)
                .name("Sergej").build();

        item = Item.builder()
                .id(1L)
                .name("Test item name")
                .owner(owner)
                .description("Test item description")
                .available(true).build();

        booker = User.builder()
                .id(2L)
                .name("Ivan").build();

        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .bookerId(booker.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3)).build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.WAITING).build();
    }

    @Test
    void addBookingItemAvailable() {
        when(userService.getUser(anyLong()))
                .thenReturn(booker);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);
        BookingDto savedBookingDto = bookingService.addBooking(booker.getId(), bookingDto);
        assertNotNull(savedBookingDto);
        assertEquals(savedBookingDto.getItemId(), bookingDto.getItemId());
    }

    @Test
    void addBookingItemUnavailable() {
        item.setAvailable(false);

        when(userService.getUser(anyLong()))
                .thenReturn(booker);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        final Long bookerId = booker.getId();
        final ItemUnavailableException exception = assertThrows(ItemUnavailableException.class,
                () -> bookingService.addBooking(bookerId, bookingDto));
        String expectedMessage = item.getId().toString();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void addBookingByUserIsOwner() {
        item.setOwner(booker);

        when(userService.getUser(anyLong()))
                .thenReturn(booker);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);

        final Long bookerId = booker.getId();
        final InvalidBookingException exception = assertThrows(InvalidBookingException.class,
                () -> bookingService.addBooking(bookerId, bookingDto));
        String expectedMessage = "Попытка бронирования " +
                "своей вещи владельцем с id = " + bookerId + "!";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void approveBookingTrue() {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoForResponse bookingDtoForResponse =
                bookingService.approveBooking(1L, 1L, true);

        assertEquals(BookingStatus.APPROVED, bookingDtoForResponse.getStatus());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void approveBookingFalse() {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoForResponse bookingDtoForResponse =
                bookingService.approveBooking(1L, 1L, false);

        assertEquals(BookingStatus.REJECTED, bookingDtoForResponse.getStatus());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void approveBookingApproved() {
        booking.setStatus(BookingStatus.APPROVED);
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        final BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> bookingService.approveBooking(1L, 1L, true));
        String expectedMessage = "Бронирование уже имеет статус " + BookingStatus.APPROVED.name();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void approveBookingRejected() {
        booking.setStatus(BookingStatus.REJECTED);
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        final BookingStatusException exception = assertThrows(BookingStatusException.class,
                () -> bookingService.approveBooking(1L, 1L, false));
        String expectedMessage = "Бронирование уже имеет статус " + BookingStatus.REJECTED.name();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getBookingInfo() {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemService.getItem(anyLong()))
                .thenReturn(item);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));

        BookingDtoForResponse bookingDtoForResponse =
                bookingService.getBookingInfo(1L, 1L);

        assertNotNull(bookingDtoForResponse);
        assertEquals(booker, bookingDtoForResponse.getBooker());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void getBookingInfoNotFound() {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final BookingNotFoundException exception = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingInfo(1L, 1L));
        String expectedMessage = String.valueOf(1L);
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getBookings() {
        Page<Booking> daoBookings = new PageImpl<>(List.of(booking));
        when(userService.getUser(anyLong()))
                .thenReturn(booker);
        when(bookingRepository.getAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository.getAllCurrentByBookerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository.getAllPastByBookerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository.getAllFutureByBookerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository
                .getAllByBookerAndStatusOrderByStartDesc(any(User.class),
                        any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(daoBookings);

        String[] statuses = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};

        for (String status : statuses) {
            Collection<BookingDtoForResponse> bookings =
                    bookingService.getBookings(booker.getId(), status, 0, 1);
            assertNotNull(bookings);
            assertEquals(1, bookings.size());
        }

        final Long bookerId = booker.getId();
        final UnknownBookingStateException exception = assertThrows(UnknownBookingStateException.class,
                () -> bookingService.getBookings(bookerId, "UNKNOWN", 0, 1));
        String expectedMessage = "Unknown state: UNKNOWN";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getBookingsOfOwner() {
        Page<Booking> daoBookings = new PageImpl<>(List.of(booking));
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(bookingRepository.getAllByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository.getAllCurrentByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository.getAllPastByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository.getAllFutureByItemOwnerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(daoBookings);
        when(bookingRepository
                .getAllByItemOwnerAndStatusOrderByStartDesc(any(User.class),
                        any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(daoBookings);

        String[] statuses = {"ALL", "CURRENT", "PAST", "FUTURE", "WAITING", "REJECTED"};

        for (String status : statuses) {
            Collection<BookingDtoForResponse> bookings =
                    bookingService.getBookingsOfOwner(owner.getId(), status, 0, 1);
            assertNotNull(bookings);
            assertEquals(1, bookings.size());
        }

        final Long ownerId = owner.getId();
        final UnknownBookingStateException exception = assertThrows(UnknownBookingStateException.class,
                () -> bookingService.getBookingsOfOwner(ownerId, "UNKNOWN", 0, 1));
        String expectedMessage = "Unknown state: UNKNOWN";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}