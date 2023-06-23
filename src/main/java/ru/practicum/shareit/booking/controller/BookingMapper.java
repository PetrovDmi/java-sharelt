package ru.practicum.shareit.booking.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ValidationException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public BookingDto convertToDto(Booking booking) {
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        return bookingDto;
    }

    public BookingOwnerDto convertToShort(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingOwnerDto bookingOwnerDto = new BookingOwnerDto();
        bookingOwnerDto.setId(booking.getId());
        bookingOwnerDto.setBookerId(booking.getBooker().getId());
        return bookingOwnerDto;
    }

    public List<BookingDto> convertToDtoListOfBooking(List<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(convertToDto(booking));
        }
        return bookingDtos;
    }

    public Booking convertToEntity(BookingRequestDto bookingRequestDto) {
        validateDate(bookingRequestDto);
        Booking booking = modelMapper.map(bookingRequestDto, Booking.class);
        return booking;
    }

    private void validateDate(BookingRequestDto bookingRequestDto) {
        if (!bookingRequestDto.getStart().isBefore(bookingRequestDto.getEnd())) {
            throw new ValidationException("Время начала брони должно быть раньше времени конца!");
        }
    }
}
