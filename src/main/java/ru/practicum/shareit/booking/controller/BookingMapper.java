package ru.practicum.shareit.booking.controller;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public BookingDto convertToDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
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
        return modelMapper.map(bookingRequestDto, Booking.class);
    }

    private void validateDate(BookingRequestDto bookingRequestDto) {
        if (!bookingRequestDto.getStart().isBefore(bookingRequestDto.getEnd())) {
            throw new ValidationException("Время начала брони должно быть раньше времени конца!");
        }
    }
}
