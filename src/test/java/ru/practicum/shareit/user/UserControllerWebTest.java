package ru.practicum.shareit.user;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.controller.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(controllers = UserController.class)
@Import(UserMapper.class)
public class UserControllerWebTest {

    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private final Map<Long, User> userTestMap = Map.of(
        1L, new User(1, "testUserOne", "testUserOne@yandex.ru"),
        2L, new User(2, "testUserTwo", "testUserTwo@yandex.ru"),
        3L, new User(3, "testUserThree", "testUserThree@yandex.ru")
    );

    @BeforeEach
    void setUp() {
        Mockito.when(userService.getUserById(1)).thenReturn(userTestMap.get(1L));
        Mockito.when(userService.getUserById(2)).thenReturn(userTestMap.get(2L));
        Mockito.when(userService.getUserById(3)).thenReturn(userTestMap.get(3L));
    }

    @Test
    void addUserShouldReturnOk() throws Exception {
        UserDto userDto = new UserDto(1, "testUserOne", "testUserOne@yandex.ru");

        mvc.perform(post("/users")
            .content(mapper.writeValueAsString(userDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.name", is(userTestMap.get(1L).getName())))
            .andExpect(jsonPath("$.email", is(userTestMap.get(1L).getEmail())));
    }

    @Test
    void addUserBadEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(1, "testUserOne", "testUserOne");

        mvc.perform(post("/users")
            .content(mapper.writeValueAsString(userDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addUserBlankEmailShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(1, "testUserOne", "");

        mvc.perform(post("/users")
            .content(mapper.writeValueAsString(userDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addUserBlankNameShouldReturnBadRequest() throws Exception {
        UserDto userDto = new UserDto(1, "", "testUserOne@yandex.ru");

        mvc.perform(post("/users")
            .content(mapper.writeValueAsString(userDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void updateUserShouldReturnOk() throws Exception {
        UserDto userDto = new UserDto(1, "", "testUserOne@yandex.ru");
        mvc.perform(patch("/users/1")
            .content(mapper.writeValueAsString(userDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.name", notNullValue()))
            .andExpect(jsonPath("$.email", notNullValue()));
    }

    @Test
    void deleteUserShouldReturnOk() throws Exception {
        mvc.perform(delete("/users/1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void getUserByIdShouldReturnOk() throws Exception {
        mvc.perform(get("/users/1"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.name", is(userTestMap.get(1L).getName())))
            .andExpect(jsonPath("$.email", is(userTestMap.get(1L).getEmail())));
    }

    @Test
    void getAllUsersShouldReturnOk() throws Exception {

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(userTestMap.get(1L),
            userTestMap.get(2L), userTestMap.get(3L)));

        mvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)))
            .andExpect(jsonPath("$[0].id", notNullValue(), Long.class))
            .andExpect(jsonPath("$[0].name", is(userTestMap.get(1L).getName())))
            .andExpect(jsonPath("$[0].email", is(userTestMap.get(1L).getEmail())));
    }
}
