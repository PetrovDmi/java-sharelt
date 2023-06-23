package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    @PositiveOrZero
    long id;
    private ItemDto item;
    private UserDto booker;
    private Status status;
    private LocalDateTime start;
    private LocalDateTime end;

}
