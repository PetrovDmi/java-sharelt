package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.controller.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.controller.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@Import({RequestMapper.class})
public class ItemRequestControllerWebTest {

    @MockBean
    private ItemMapper itemMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;
    @MockBean
    private RequestService requestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    LocalDateTime created = LocalDateTime.of(2023, 5, 19,
            10, 0, 0);

    private final Map<Long, User> userTestMap = Map.of(
            1L, new User(1, "testUserOne", "testUserOne@yandex.ru"),
            2L, new User(2, "testUserTwo", "testUserTwo@yandex.ru"),
            3L, new User(3, "testUserThree", "testUserThree@yandex.ru")
    );

    private final Map<Long, ItemRequest> itemRequestTestMap = Map.of(
            1L, new ItemRequest(1, "Хочу дрель", userTestMap.get(1L),
                    created, null),
            2L, new ItemRequest(2, "Хочу дрель", userTestMap.get(2L),
                    created, null),
            3L, new ItemRequest(3, "Хочу молоток", userTestMap.get(1L),
                    created, null)
    );

    @BeforeEach
    void setUp() {
        Mockito.when(userService.getUserById(1)).thenReturn(userTestMap.get(1L));
        Mockito.when(userService.getUserById(2)).thenReturn(userTestMap.get(2L));
        Mockito.when(userService.getUserById(3)).thenReturn(userTestMap.get(3L));
        Mockito.when(userService.isUserExists(1L)).thenReturn(true);
        Mockito.when(userService.isUserExists(2L)).thenReturn(true);
        Mockito.when(userService.isUserExists(3L)).thenReturn(true);
        Mockito.when(userService.isUserExists(4L)).thenReturn(false);

        Mockito.when(requestService.getItemRequestById(1L, 1L))
                .thenReturn(itemRequestTestMap.get(1L));
        Mockito.when(requestService.getItemRequestById(2L, 2L))
                .thenReturn(itemRequestTestMap.get(2L));
        Mockito.when(requestService.getItemRequestById(3L, 1L))
                .thenReturn(itemRequestTestMap.get(3L));

        Mockito.when(requestService.getUserRequestsById(1L))
                .thenReturn(List.of(itemRequestTestMap.get(1L), itemRequestTestMap.get(3L)));
        Mockito.when(requestService.getUserRequestsById(4L))
                .thenThrow(new NotFoundException("Пользователь не найден"));
        Mockito.when(requestService.getOtherUsersRequests(2L, 0, 10))
                .thenReturn(List.of(itemRequestTestMap.get(2L)));
        Mockito.when(requestService.getItemRequestById(1L, 1L))
                .thenReturn(itemRequestTestMap.get(1L));

        Mockito.when(itemService.getItemsByRequestId(itemRequestTestMap.get(1L).getId()))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(requestService.addItemRequest(Mockito.any(ItemRequest.class), Mockito.anyLong()))
                .thenReturn(itemRequestTestMap.get(1L));

    }

    @Test
    void contextTest() {
    }

    @Test
    void addItemRequestBlackDescriptionShouldReturnBadRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "", 2L,
                created, null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addItemRequestBadIdShouldReturnBadRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(-1, "Need Healing",
                2L, created, null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addItemRequestRequesterShouldReturnBadRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Need Healing",
                -2L, created, null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addItemRequestShouldReturnAllOk() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Need Healing",
                1L, created, null);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void getUserRequestsShouldReturnListOfRequests() throws Exception {
        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getUserRequestsByIdUserNotExistsShouldReturnExceptionNotFound() throws Exception {
        mvc.perform(get("/requests").header("X-Sharer-User-Id", 4))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(404))));
    }

    @Test
    void getOtherUsersRequestsShouldReturnListOfRequests() throws Exception {

        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 2))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getItemRequestByIdShouldReturnItemRequest() throws Exception {

        mvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestTestMap.get(1L).getId()),
                        Long.class))
                .andExpect(jsonPath("$.description",
                        is(itemRequestTestMap.get(1L).getDescription())))
                .andExpect(jsonPath("$.requesterId",
                        is(itemRequestTestMap.get(1L).getRequester().getId()), Long.class));
    }

}
