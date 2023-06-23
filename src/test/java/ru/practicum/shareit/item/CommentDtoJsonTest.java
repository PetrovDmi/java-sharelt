package ru.practicum.shareit.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.CommentDto;

@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;
    LocalDateTime created = LocalDateTime.of(2023, 5, 19,
        10, 0, 0);

    @Test
    void tesCommentDtoJsonTest() throws Exception {
        CommentDto commentDto = new CommentDto(1, "Test", "Johny", created);

        JsonContent<CommentDto> dto = commentDtoJacksonTester.write(commentDto);
        assertThat(dto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dto).extractingJsonPathStringValue("$.text").isEqualTo("Test");
        assertThat(dto).extractingJsonPathStringValue("$.authorName").isEqualTo("Johny");
        assertThat(dto).extractingJsonPathValue("$.created")
            .isEqualTo("2023-05-19T10:00:00");
    }
}
