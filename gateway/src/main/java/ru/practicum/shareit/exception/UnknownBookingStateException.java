package ru.practicum.shareit.exception;

public class UnknownBookingStateException extends IllegalArgumentException {
    public UnknownBookingStateException(String message) {
        super(message);
    }
}