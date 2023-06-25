package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = new ItemDto(1L, "Дрель", "Какая-то дрель",
                true, null, null, null, null);

        JsonContent<ItemDto> dto = itemDtoJacksonTester.write(itemDto);

        assertThat(dto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dto).extractingJsonPathStringValue("$.name")
                .isEqualTo("Дрель");
        assertThat(dto).extractingJsonPathValue("$.description")
                .isEqualTo("Какая-то дрель");
        assertThat(dto).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(dto).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(dto).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(dto).extractingJsonPathValue("$.comments").isNull();
        assertThat(dto).extractingJsonPathValue("$.requestId").isNull();
    }
}
