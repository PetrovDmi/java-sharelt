package ru.practicum.shareit.item;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.controller.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.CommentMapper;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.controller.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@WebMvcTest(controllers = ItemController.class)
@Import({ItemMapper.class, BookingMapper.class, CommentMapper.class})
public class ItemControllerWebTest {

    @MockBean
    ItemService itemService;
    @MockBean
    BookingService bookingService;
    @MockBean
    CommentRepository commentRepository;
    @MockBean
    UserService userService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private PageRequest page = org.springframework.data.domain.PageRequest.of(0, 10);
    LocalDateTime created = LocalDateTime.of(2023, 5, 19,
        10, 0, 0);

    private final Map<Long, User> userTestMap = Map.of(
        1L, new User(1, "testUserOne", "testUserOne@yandex.ru"),
        2L, new User(2, "testUserTwo", "testUserTwo@yandex.ru"),
        3L, new User(3, "testUserThree", "testUserThree@yandex.ru")
    );

    private final Map<Long, Item> itemTestMap = Map.of(
        1L, new Item(1, userTestMap.get(1L).getId(), "Дрель",
            "Описание дрели", true, null),
        2L, new Item(2, userTestMap.get(2L).getId(), "Молоток",
            "Описание молотка", true, null),
        3L, new Item(3, userTestMap.get(2L).getId(), "Кувалда",
            "Описание кувалды", true, null),
        4L, new Item(4, userTestMap.get(2L).getId(), "Кувалда мини",
            "Описание кувалды мини", false, null)
    );

    Booking booking = new Booking(1, itemTestMap.get(2L), userTestMap.get(3L), Status.APPROVED,
        created.minusHours(4), created.minusHours(1));

    Comment comment = new Comment(1, "All Good", itemTestMap.get(1L),
        userTestMap.get(2L), created);
    CommentDto commentTestDto = new CommentDto(1, "All Cool", "Jonhy", created);

    @BeforeEach
    void setUp() {
        Mockito.when(userService.isUserExists(1L)).thenReturn(true);
        Mockito.when(userService.isUserExists(2L)).thenReturn(true);
        Mockito.when(userService.isUserExists(3L)).thenReturn(true);
        Mockito.when(userService.isUserExists(4L)).thenReturn(false);

        Mockito.when(itemService.getItem(1L)).thenReturn(itemTestMap.get(1L));
        Mockito.when(itemService.getItem(2L)).thenReturn(itemTestMap.get(2L));
        Mockito.when(itemService.getItem(3L)).thenReturn(itemTestMap.get(3L));
        Mockito.when(itemService.getItem(4L)).thenReturn(itemTestMap.get(4L));

        Mockito.when(userService.getUserById(1L)).thenReturn(userTestMap.get(1L));
        Mockito.when(userService.getUserById(2L)).thenReturn(userTestMap.get(2L));
        Mockito.when(userService.getUserById(3L)).thenReturn(userTestMap.get(3L));

        Mockito.when(itemService.getItemsByNameOrDescriptionSearch("123", 0, 10))
            .thenReturn(List.of(itemTestMap.get(1L), itemTestMap.get(2L), itemTestMap.get(3L)));
        Mockito.when(itemService.getUserItems(1L, 0, 10))
            .thenReturn(List.of(itemTestMap.get(1L)));
        Mockito.when(itemService.getCommentById(1L)).thenReturn(comment);
        Mockito.when(itemService.isUserAnItemOwner(2L,itemTestMap.get(2L))).thenReturn(true);
        Mockito.when(bookingService.getItemLastBooking(2L)).thenReturn(booking);
    }

    @Test
    void contextTest() {
    }

    @Test
    void addItemShouldReturnAllOk() throws Exception {
        ItemDto itemDto = new ItemDto(1, "Дрель", "Описание дрели", true,
            null, null, null, null);

        mvc.perform(post("/items")
            .header("X-Sharer-User-Id", 1)
            .content(mapper.writeValueAsString(itemDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void addItemBlankNameShouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto(1, "", "Описание дрели", true,
            null, null, null, null);

        mvc.perform(post("/items")
            .header("X-Sharer-User-Id", 1)
            .content(mapper.writeValueAsString(itemDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400, 404))));
    }

    @Test
    void addItemBlankDescriptionShouldReturnBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto(1, "Дрель", "", true,
            null, null, null, null);

        mvc.perform(post("/items")
            .header("X-Sharer-User-Id", 4)
            .content(mapper.writeValueAsString(itemDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400, 404))));
    }

    @Test
    void updateItemShouldReturnAllOk() throws Exception {
        ItemDto itemDto = new ItemDto(1, "Дрель", "Описание дрели", true,
            null, null, null, null);

        mvc.perform(patch("/items/1")
            .header("X-Sharer-User-Id", 1)
            .content(mapper.writeValueAsString(itemDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class));
    }

    @Test
    void deleteUserByIdShouldReturnAllOk() throws Exception {
        mvc.perform(delete("/items/1").header("X-Sharer-User-Id", 1))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void getItemByIdByUserShouldReturnItem() throws Exception {
        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemTestMap.get(1L).getId()), Long.class))
            .andExpect(jsonPath("$.name", is(itemTestMap.get(1L).getName())))
            .andExpect(jsonPath("$.description", is(itemTestMap.get(1L).getDescription())));
    }

    @Test
    void getItemByIdByOwnerShouldReturnItem() throws Exception {
        mvc.perform(get("/items/2").header("X-Sharer-User-Id", 2))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(itemTestMap.get(2L).getId()), Long.class))
            .andExpect(jsonPath("$.name", is(itemTestMap.get(2L).getName())))
            .andExpect(jsonPath("$.description", is(itemTestMap.get(2L).getDescription())))
            .andExpect(jsonPath("$.lastBooking.id",is(booking.getId()), Long.class));
    }

    @Test
    void getItemsByUserSearchShouldReturnItems() throws Exception {
        mvc.perform(get("/items/search")
            .header("X-Sharer-User-Id", 1)
            .param("text", "123"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getAllUserItemsShouldReturnItems() throws Exception {
        mvc.perform(get("/items").header("X-Sharer-User-Id", 1))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void addCommentShouldAddAComment() throws Exception {
        mvc.perform(post("/items/1/comment")
            .header("X-Sharer-User-Id", 1)
            .content(mapper.writeValueAsString(commentTestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void unknownMethodShouldReturnInternalServer() throws Exception {
        mvc.perform(put("/itemsnew"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(404, 500))));
    }

}
