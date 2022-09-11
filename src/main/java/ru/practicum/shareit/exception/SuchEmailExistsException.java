package ru.practicum.shareit.exception;

public class SuchEmailExistsException extends RuntimeException {
    public SuchEmailExistsException(String message) {
        super(message);
    }
}
