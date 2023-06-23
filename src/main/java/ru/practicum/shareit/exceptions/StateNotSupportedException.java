package ru.practicum.shareit.exceptions;

public class StateNotSupportedException extends RuntimeException {

    public StateNotSupportedException(String message) {
        super(message);
    }
}
