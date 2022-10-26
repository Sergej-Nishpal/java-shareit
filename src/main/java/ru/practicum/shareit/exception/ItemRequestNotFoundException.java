package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(long id) {
        super(String.format("Запрос с id = %d не найден!", id));
    }
}