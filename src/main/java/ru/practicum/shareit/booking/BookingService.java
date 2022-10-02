package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;

import java.util.Collection;

public interface BookingService {

    BookingDto addBooking(Long userId, BookingDto bookingDto);

    BookingGetDto approveBooking(long userId, long bookingId, boolean approved);

    BookingGetDto getBookingInfo(long userId, long bookingId);

    Collection<BookingGetDto> getBookings(long userId, BookingStatus state);

    void validateBookingExists(long id);
}
