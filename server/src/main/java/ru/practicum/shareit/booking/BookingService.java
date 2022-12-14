package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;

import java.util.Collection;

public interface BookingService {

    BookingDtoForResponse addBooking(Long userId, BookingDto bookingDto);

    BookingDtoForResponse approveBooking(long userId, long bookingId, boolean approved);

    BookingDtoForResponse getBookingInfo(long userId, long bookingId);

    Collection<BookingDtoForResponse> getBookings(long userId, String state, int from, int size);

    Collection<BookingDtoForResponse> getBookingsOfOwner(long userId, String state, int from, int size);
}