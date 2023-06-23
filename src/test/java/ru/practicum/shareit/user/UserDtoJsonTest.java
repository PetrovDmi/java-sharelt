package ru.practicum.shareit.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> userDtoJacksonTester;

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = new UserDto(1, "John", "jonnysilverhand@yandex.ru");

        JsonContent<UserDto> dto = userDtoJacksonTester.write(userDto);

        assertThat(dto).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(dto).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(dto).extractingJsonPathStringValue("$.email")
            .isEqualTo("jonnysilverhand@yandex.ru");
    }

}
