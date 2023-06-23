package ru.practicum.shareit.booking.service;

import java.util.List;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {

    void addBooking(Booking booking, long userId, long itemId);

    void updateBooking(long bookingId, long userId, boolean approval);

    Booking getBookingById(long bookingId);

    Booking getBookingById(long bookingId, long userId);

    List<Booking> getAllBookingOfUserWithState(long userId, String state, int from, int size);

    List<Booking> getAllBookingForItemsOfOwnerWithState(long userId, String state, int from,
        int size);

    Booking getItemLastBooking(long itemId);

    Booking getItemNextBooking(long itemId);

}
