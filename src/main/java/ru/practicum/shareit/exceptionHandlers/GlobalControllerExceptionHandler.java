package ru.practicum.shareit.exceptionHandlers;

import java.util.Map;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exceptionHandlers.Entity.ErrorResponse;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.StateNotSupportedException;

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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRunTimeException(final RuntimeException exception) {
        log.error("Неизвестная ошибка");
        return Map.of("Что-то пошло не так", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleStateNotSupportedException(
        final StateNotSupportedException exception) {
        log.error("Ошибка получения состояния бронирования");
        return new ErrorResponse(400, "Bad Request", exception.getMessage());
    }
}
