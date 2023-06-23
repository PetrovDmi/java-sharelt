package ru.practicum.shareit.booking;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.controller.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(controllers = BookingController.class)
@Import(BookingMapper.class)
public class BookingControllerWebTest {

    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    private final LocalDateTime curDate = LocalDateTime.now();

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

    private Booking bookingOne;
    private Booking bookingTwo;
    private Booking bookingThree;
    private Booking bookingFour;
    private Booking bookingFive;

    @BeforeEach
    void setUp() {
        bookingOne = new Booking(1, itemTestMap.get(1L), userTestMap.get(2L), Status.WAITING,
            curDate.plusMinutes(30), curDate.plusHours(2));
        bookingTwo = new Booking(2, itemTestMap.get(1L), userTestMap.get(3L), Status.WAITING,
            curDate.minusMinutes(35), curDate.plusHours(4));
        bookingThree = new Booking(3, itemTestMap.get(2L), userTestMap.get(3L), Status.APPROVED,
            curDate.minusMinutes(55), curDate.minusMinutes(10));
        bookingFour = new Booking(4, itemTestMap.get(3L), userTestMap.get(3L), Status.APPROVED,
            curDate.plusMinutes(10), curDate.plusMinutes(50));
        bookingFive = new Booking(5, itemTestMap.get(1L), userTestMap.get(3L), Status.APPROVED,
            curDate.minusMinutes(10), curDate.plusMinutes(20));

        Mockito.when(bookingService.getBookingById(bookingOne.getId()))
            .thenReturn(bookingOne);
        Mockito.when(bookingService.getBookingById(bookingTwo.getId()))
            .thenReturn(bookingTwo);
        Mockito.when(bookingService.getBookingById(bookingThree.getId()))
            .thenReturn(bookingThree);
        Mockito.when(bookingService.getBookingById(bookingFour.getId()))
            .thenReturn(bookingFour);
        Mockito.when(bookingService.getBookingById(bookingFive.getId()))
            .thenReturn(bookingFive);

        Mockito.when(bookingService.getBookingById(bookingOne.getId(),2))
            .thenReturn(bookingOne);
        Mockito.when(bookingService.getBookingById(bookingTwo.getId(),3))
            .thenReturn(bookingTwo);
        Mockito.when(bookingService.getBookingById(bookingThree.getId(),3))
            .thenReturn(bookingThree);
        Mockito.when(bookingService.getBookingById(bookingFour.getId(),3))
            .thenReturn(bookingFour);
        Mockito.when(bookingService.getBookingById(bookingFive.getId(),3))
            .thenReturn(bookingFive);
    }

    @Test
    void addBookingBadStartTimeShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1,
            itemTestMap.get(1L).getId(), curDate.minusMinutes(30), curDate.plusHours(2));

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 2)
            .content(mapper.writeValueAsString(bookingRequestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addBookingBadEndTimeShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1,
            itemTestMap.get(1L).getId(), curDate.plusHours(2), curDate.minusHours(2));

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 2)
            .content(mapper.writeValueAsString(bookingRequestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addBookingEndBeforeStartShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1,
            itemTestMap.get(1L).getId(), curDate.plusHours(4), curDate.plusHours(3));

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 2)
            .content(mapper.writeValueAsString(bookingRequestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addBookingBadItemIdShouldReturnBadRequest() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1, -5,
            curDate.plusHours(2), curDate.minusHours(2));

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 2)
            .content(mapper.writeValueAsString(bookingRequestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void addBookingShouldReturnOk() throws Exception {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1,
            bookingOne.getItem().getId(), curDate.plusMinutes(30), curDate.plusHours(2));

        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 2)
            .content(mapper.writeValueAsString(bookingRequestDto))
            .characterEncoding(StandardCharsets.UTF_8)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class))
            .andExpect(jsonPath("$.item", notNullValue()))
            .andExpect(jsonPath("$.booker", notNullValue()))
            .andExpect(jsonPath("$.status", is(bookingOne.getStatus().name())));
    }

    @Test
    void updateBookingShouldReturnOk() throws Exception {
        mvc.perform(patch("/bookings/1").header("X-Sharer-User-Id", 2)
            .param("approved", "true"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue(), Long.class));
    }

    @Test
    void getBookingByIdShouldReturnOk() throws Exception {
        mvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 2))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void getCurrentBookingForUserShouldReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 3)
        .param("from", String.valueOf(-1)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void getCurrentBookingForUserShouldReturnOk() throws Exception {
        Mockito.when(bookingService.getAllBookingOfUserWithState(3,"ALL",0,10))
            .thenReturn(List.of(bookingTwo,bookingThree,bookingFour,bookingFive));
        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 3))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(4)));
    }

    @Test
    void getCurrentBookingForOwnerShouldReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
            .param("from", String.valueOf(-1)))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400))));
    }

    @Test
    void getCurrentBookingForOwnerShouldReturnOk() throws Exception {
        Mockito.when(bookingService.getAllBookingForItemsOfOwnerWithState(1,"ALL",
            0,10))
            .thenReturn(List.of(bookingTwo,bookingThree,bookingFive));
        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    void getCurrentBookingForUserBadStatusShouldReturnBadRequest() throws Exception {
        Mockito.when(bookingService.getAllBookingOfUserWithState(1,"COMPLEX",
            0,10))
            .thenThrow(new RuntimeException("Unknown state: " + "COMPLEX"));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
        .param("state","COMPLEX"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().is(in(List.of(400,500))));
    }
}
