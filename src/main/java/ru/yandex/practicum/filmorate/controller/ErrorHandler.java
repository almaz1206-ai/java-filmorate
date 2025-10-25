package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.util.Objects;

@Slf4j
@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class ErrorHandler {
    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        log.warn("Объект не найден: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final MethodArgumentNotValidException e) {
        String field = Objects.requireNonNull(e.getBindingResult().getFieldError()).getField();
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();

        log.warn("Ошибка валидации: {}", String.format("Поле %s %s", field, errorMessage));
        return new ErrorResponse(String.format("Поле %s %s", field, errorMessage));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Непредвиденная ошибка: ", e);
        return new ErrorResponse("Произошла непредвиденная ошибка");
    }

    private static class ErrorResponse {
        String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

}
