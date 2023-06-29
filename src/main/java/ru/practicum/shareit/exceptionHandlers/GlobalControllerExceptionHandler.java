package ru.practicum.shareit.exceptionHandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptionHandlers.Entity.ErrorResponse;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StateNotSupportedException;

import javax.validation.ValidationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException exception) {
        log.error("Предмет или пользователь не обнаружен ");
        return Map.of("Пользователь или предмет не найден ", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException exception) {
        log.error("Не пройдена валидация для создания сущност");
        return Map.of("Ошибка валидации", exception.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRunTimeException(final Throwable throwable) {
        log.error("Произошла неизвестная ошибка", throwable);
        return Map.of("message", "Возникла ошибка сервера, обратитесь к администратору");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleStateNotSupportedException(
            final StateNotSupportedException exception) {
        log.error("Ошибка получения состояния бронирования");
        return new ErrorResponse(500, "Bad Request", exception.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleDuplicateKeyException(DuplicateKeyException ex) {
        String message = "Пользователь с такой электронной почтой уже существует";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        String message = "Пользователь с таким ID уже существует";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
