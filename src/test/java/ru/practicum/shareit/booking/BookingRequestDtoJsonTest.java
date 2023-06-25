package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingRequestDtoJsonTest {
    @Autowired
    private JacksonTester<BookingRequestDto> jsonBookingRequestDto;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.of(2023, 5, 19,
                10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 5, 19,
                12, 0, 0);

        BookingRequestDto bookingRequestDto = new BookingRequestDto(1, 2, start, end);

        JsonContent<BookingRequestDto> dto = jsonBookingRequestDto.write(bookingRequestDto);

        assertThat(dto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dto).extractingJsonPathNumberValue("$.itemId").isEqualTo(2);
        assertThat(dto).extractingJsonPathValue("$.start").isEqualTo("2023-05-19T10:00:00");
        assertThat(dto).extractingJsonPathValue("$.end").isEqualTo("2023-05-19T12:00:00");
    }

}
