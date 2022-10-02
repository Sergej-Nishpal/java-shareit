package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingGetDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid BookingDto bookingDto) {

        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingGetDto approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam(name = "approved") boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingGetDto getBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId) {
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingGetDto> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "state", required = false,
                                                      defaultValue = "ALL") BookingStatus state) {
        return bookingService.getBookings(userId, state);
    }
}
