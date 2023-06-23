package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

    @PositiveOrZero
    private long id;
    @PositiveOrZero
    private long itemId;
    @FutureOrPresent(message = "Дата не должна быть в прошлом")
    @NotNull(message = "Дата не должна быть пустой")
    private LocalDateTime start;
    @FutureOrPresent(message = "Дата не должна быть в прошлом")
    @NotNull(message = "Дата не должна быть пустой")
    private LocalDateTime end;
}
