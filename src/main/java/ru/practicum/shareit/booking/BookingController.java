package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public BookingDtoForResponse addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody @Valid BookingDto bookingDto) {
        log.debug("Добавление бронирования пользователем с id = {}.", userId);
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoForResponse approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long bookingId,
                                                @RequestParam(name = "approved") boolean approved) {
        log.debug("Изменение статуса бронирования пользователем с id = {}.", userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoForResponse getBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long bookingId) {
        log.debug("Получение информации о бронировании с id = {} пользователем с id = {}.", bookingId, userId);
        return bookingService.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoForResponse> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(name = "state", required = false,
                                                                 defaultValue = "ALL") String state,
                                                         @PositiveOrZero @RequestParam(name = "from",
                                                                 defaultValue = "0") int from,
                                                         @Positive @RequestParam(name = "size",
                                                                 defaultValue = "10") int size) {
        log.debug("Получение информации о бронированиях со статусом \"{}\" пользователем с id = {}.", state, userId);
        return bookingService.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoForResponse> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                                @RequestParam(name = "state", required = false,
                                                                        defaultValue = "ALL") String state,
                                                                @PositiveOrZero @RequestParam(name = "from",
                                                                        defaultValue = "0") int from,
                                                                @Positive @RequestParam(name = "size",
                                                                        defaultValue = "10") int size) {
        log.debug("Получение информации о бронированиях со статусом \"{}\" владельцем с id = {}.", state, userId);
        return bookingService.getBookingsOfOwner(userId, state, from, size);
    }
}