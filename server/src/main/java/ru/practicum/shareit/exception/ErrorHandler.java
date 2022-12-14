package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ItemUnavailableException.class, BookingStatusException.class, IncorrectCommentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final RuntimeException e) {
        log.error("400 - Ошибка при обработке входных данных: {} ", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailExists(final RuntimeException e) {
        log.error("409 - Конфликт данных: {} ", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UnauthorizedOperationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleUnauthorizedOperation(final RuntimeException e) {
        log.error("403 - Доступ запрещён: {} ", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class,
            BookingNotFoundException.class, InvalidBookingException.class, ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        log.error("404 - Объект не найден: {} ", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final RuntimeException e) {
        log.error("500 - Внутренняя ошибка сервера: {} ", e.getMessage(), e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }
}