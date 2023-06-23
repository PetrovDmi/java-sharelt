package ru.practicum.shareit.booking.dto;

import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingOwnerDto {

    @PositiveOrZero
    private long id;
    @PositiveOrZero
    private long bookerId;
}
