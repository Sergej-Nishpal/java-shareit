package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class BookingDto {
    private Long id;
    private Long itemId;
    private Long bookerId;
    @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss")
    private LocalDateTime start;
    @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss")
    private LocalDateTime end;
    private BookingStatus status;
}