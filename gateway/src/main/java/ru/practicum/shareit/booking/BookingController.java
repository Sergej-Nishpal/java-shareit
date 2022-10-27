package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	@Validated({Marker.OnCreate.class})
	public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
									 @RequestBody @Valid BookingDto bookingDto) {
		log.debug("Добавление бронирования пользователем с id = {}.", userId);
		return bookingClient.addBooking(userId, bookingDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") long userId,
												@PathVariable long bookingId,
												@RequestParam(name = "approved") boolean approved) {
		log.debug("Изменение статуса бронирования пользователем с id = {}.", userId);
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBookingInfo(@RequestHeader("X-Sharer-User-Id") long userId,
												@PathVariable long bookingId) {
		log.debug("Получение информации о бронировании с id = {} пользователем с id = {}.", bookingId, userId);
		return bookingClient.getBookingInfo(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") long userId,
														 @RequestParam(name = "state", required = false,
																 defaultValue = "ALL") String state,
														 @PositiveOrZero @RequestParam(name = "from",
																 defaultValue = "0") int from,
														 @Positive @RequestParam(name = "size",
																 defaultValue = "10") int size) {
		log.debug("Получение информации о бронированиях со статусом \"{}\" пользователем с id = {}.", state, userId);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOfOwner(@RequestHeader("X-Sharer-User-Id") long userId,
																@RequestParam(name = "state", required = false,
																		defaultValue = "ALL") String state,
																@PositiveOrZero @RequestParam(name = "from",
																		defaultValue = "0") int from,
																@Positive @RequestParam(name = "size",
																		defaultValue = "10") int size) {
		log.debug("Получение информации о бронированиях со статусом \"{}\" владельцем с id = {}.", state, userId);
		return bookingClient.getBookingsOfOwner(userId, state, from, size);
	}
}
