package ru.practicum.shareit.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(long id) {
        super(String.format("Вещь с id = %d не найдена!", id));
    }
}