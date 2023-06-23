package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> jsonBookingDto;

    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "Дрель", "Какая-то дрель",
            true, null, null, null, null);
        userDto = new UserDto(1L, "Jonny", "cats@yandex.ru");
    }

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 5, 19,
            10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 5, 19,
            12, 0, 0);

        BookingDto bookingDto = new BookingDto(1L, itemDto, userDto, Status.WAITING,
            start, end);

        JsonContent<BookingDto> dto = jsonBookingDto.write(bookingDto);

        assertThat(dto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dto).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
        assertThat(dto).extractingJsonPathValue("$.start").isEqualTo("2023-05-19T10:00:00");
    }

}
