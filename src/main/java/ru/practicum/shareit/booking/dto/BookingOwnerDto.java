package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOwnerDto {

    @PositiveOrZero
    private long id;
    @PositiveOrZero
    private long bookerId;
}
