package ru.practicum.shareit.booking.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.validation.Marker;
import ru.practicum.shareit.validation.startend.StartBeforeEndValid;
import ru.practicum.shareit.validation.startend.StartEndDated;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@StartBeforeEndValid(start = "start", end = "end")
public class BookingDto implements StartEndDated {

    private Long id;

    @NotNull(groups = Marker.OnCreate.class)
    private Long itemId;

    private Long bookerId;

    @NotNull(groups = Marker.OnCreate.class)
    @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime start;

    @NotNull(groups = Marker.OnCreate.class)
    @DateTimeFormat(pattern = "YYYY-MM-DD'T'HH:mm:ss")
    @FutureOrPresent
    private LocalDateTime end;

    private BookingStatus status;
}