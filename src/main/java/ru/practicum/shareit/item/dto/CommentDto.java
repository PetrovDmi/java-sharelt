package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    @PositiveOrZero
    private long id;
    @NotBlank(message = "Комментарий не должен быть пустым.")
    private String text;
    private String authorName;
    private LocalDateTime created;
}
