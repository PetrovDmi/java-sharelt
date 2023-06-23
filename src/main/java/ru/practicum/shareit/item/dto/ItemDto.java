package ru.practicum.shareit.item.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    @PositiveOrZero
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private BookingOwnerDto lastBooking;
    private BookingOwnerDto nextBooking;
    private List<CommentDto> comments;
    @PositiveOrZero
    private Long requestId;

}
