package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingMapperTest {

    @Autowired
    private JacksonTester<Booking> jsonBooking;

    @Autowired
    private JacksonTester<BookingDtoForResponse> jsonBookingDtoForResponse;

    @Test
    void toBooking() throws Exception {
        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 10, 21, 15, 30, 0))
                .end(LocalDateTime.of(2022, 10, 21, 19, 30, 0))
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<Booking> result = jsonBooking.write(booking);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-10-21T15:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-10-21T19:30:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void toBookingDtoForResponse() throws IOException {
        BookingDtoForResponse bookingDtoForResponse = BookingDtoForResponse.builder()
                .id(1L)
                .start(LocalDateTime.of(2022, 10, 21, 15, 30, 0))
                .end(LocalDateTime.of(2022, 10, 21, 19, 30, 0))
                .status(BookingStatus.WAITING)
                .build();

        JsonContent<BookingDtoForResponse> result = jsonBookingDtoForResponse.write(bookingDtoForResponse);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-10-21T15:30:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2022-10-21T19:30:00");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}