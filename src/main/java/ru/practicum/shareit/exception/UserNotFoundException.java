package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long id) {
        super(String.format("Пользователь с id = %d не найден!", id));
    }
}