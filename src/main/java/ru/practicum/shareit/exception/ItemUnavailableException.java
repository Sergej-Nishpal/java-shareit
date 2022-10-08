package ru.practicum.shareit.exception;


public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(long id) {
        super(String.format("Вещь с id = %d недоступна для бронирования!", id));
    }
}