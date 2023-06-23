package ru.practicum.shareit.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> dtoJacksonTester;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2023, 5, 19,
            10, 0, 0);
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L,"Test",
            1L,created,null);


        JsonContent<ItemRequestDto> dto = dtoJacksonTester.write(itemRequestDto);

        assertThat(dto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dto).extractingJsonPathStringValue("$.description")
            .isEqualTo("Test");
        assertThat(dto).extractingJsonPathValue("$.created")
            .isEqualTo("2023-05-19T10:00:00");
        assertThat(dto).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(dto).extractingJsonPathValue("$.items").isNull();
    }

}
