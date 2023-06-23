package ru.practicum.shareit.exceptionHandlers.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {

    private int code;
    private String status;
    private String error;
}
