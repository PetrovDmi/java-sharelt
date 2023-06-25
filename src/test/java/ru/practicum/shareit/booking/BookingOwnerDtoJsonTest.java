package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOwnerDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOwnerDtoJsonTest {
    @Autowired
    private JacksonTester<BookingOwnerDto> jsonBookingOwnerDto;

    @Test
    void testBookingOwnerDto() throws Exception {
        BookingOwnerDto bookingOwnerDto = new BookingOwnerDto(1, 2);

        JsonContent<BookingOwnerDto> dto = jsonBookingOwnerDto.write(bookingOwnerDto);

        assertThat(dto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dto).extractingJsonPathNumberValue("$.bookerId").isEqualTo(2);
    }
}
