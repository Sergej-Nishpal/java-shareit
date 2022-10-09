package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(long id) {
        super(String.format("Бронирование с id = %d не найдено!", id));
    }
}