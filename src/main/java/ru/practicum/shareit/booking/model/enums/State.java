package ru.practicum.shareit.booking.model.enums;

import ru.practicum.shareit.exceptions.StateNotSupportedException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State convert(String source) {
        try {
            return State.valueOf(source);
        } catch (Exception e) {
            throw new StateNotSupportedException("Unknown state: " + source);
        }
    }
}
