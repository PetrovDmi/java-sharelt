package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@AllArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingDto addBooking(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на добавление брони предмета");
        Booking booking = mapper.convertToEntity(bookingRequestDto);
        bookingService.addBooking(booking, userId, bookingRequestDto.getItemId());
        return mapper.convertToDto(bookingService.getBookingById(booking.getId()));
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestParam boolean approved, @PathVariable Long bookingId,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на обновление брони предмета");
        bookingService.updateBooking(bookingId, userId, approved);
        return mapper.convertToDto(bookingService.getBookingById(bookingId));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на получение информации о брони предмета");
        return mapper.convertToDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> getCurrentBookingForUser(
            @RequestParam(defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.debug("Получен запрос на получение информации о брони предметов пользователя");
        return mapper.convertToDtoListOfBooking(
                bookingService.getAllBookingOfUserWithState(userId, state, from, size));
    }

    @GetMapping("/owner")
    public List<BookingDto> getCurrentBookingForOwner(
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.debug("Получен запрос на получение информации о брони предметов владельца");
        return mapper.convertToDtoListOfBooking(
                bookingService.getAllBookingForItemsOfOwnerWithState(userId, state, from, size));
    }
}
