package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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
        userService.addUser(userTestMap.get(1L));
        userService.addUser(userTestMap.get(2L));
        userService.addUser(userTestMap.get(3L));

        itemService.addItem(itemTestMap.get(1L));
        itemService.addItem(itemTestMap.get(2L));
        itemService.addItem(itemTestMap.get(3L));
        itemService.addItem(itemTestMap.get(4L));

        bookingOne = new Booking(1, itemTestMap.get(1L), userTestMap.get(2L), Status.WAITING,
                LocalDateTime.now().minusMinutes(30), LocalDateTime.now().plusHours(2));
        bookingTwo = new Booking(2, itemTestMap.get(1L), userTestMap.get(3L), Status.WAITING,
                LocalDateTime.now().minusMinutes(35), LocalDateTime.now().plusHours(4));
        bookingThree = new Booking(3, itemTestMap.get(2L), userTestMap.get(3L), Status.APPROVED,
                LocalDateTime.now().minusMinutes(55), LocalDateTime.now().minusMinutes(10));
        bookingFour = new Booking(4, itemTestMap.get(3L), userTestMap.get(3L), Status.APPROVED,
                LocalDateTime.now().plusMinutes(10), LocalDateTime.now().plusMinutes(50));
        bookingFive = new Booking(5, itemTestMap.get(1L), userTestMap.get(3L), Status.APPROVED,
                LocalDateTime.now().minusMinutes(10), LocalDateTime.now().plusMinutes(20));
    }

    @Test
    void addBookingTestShouldWriteToDbAndReturn() {
        bookingService
                .addBooking(bookingOne, userTestMap.get(2L).getId(), itemTestMap.get(1L).getId());

        Booking booking = bookingService.getBookingById(bookingOne.getId());

        Assertions.assertEquals(bookingOne.getId(), booking.getId());
        Assertions.assertEquals(bookingOne.getItem().getId(), booking.getItem().getId());
        Assertions.assertEquals(bookingOne.getBooker().getId(), booking.getBooker().getId());
        Assertions.assertEquals(bookingOne.getStatus(), booking.getStatus());
        Assertions.assertEquals(bookingOne.getBookingStart().format(DATE_FORMAT),
                booking.getBookingStart().format(DATE_FORMAT));
        Assertions.assertEquals(bookingOne.getBookingEnd().format(DATE_FORMAT),
                booking.getBookingEnd().format(DATE_FORMAT));
    }

    @Test
    void updateBookingTestShouldUpdateStatusApproved() {
        bookingService
                .addBooking(bookingOne, userTestMap.get(2L).getId(), itemTestMap.get(1L).getId());

        bookingService.updateBooking(bookingOne.getId(), itemTestMap.get(1L).getUserId(),
                true);
        Booking booking = bookingService.getBookingById(bookingOne.getId());

        Assertions.assertEquals(Status.APPROVED, booking.getStatus());
    }

    @Test
    void updateBookingTestShouldUpdateStatusRejected() {
        bookingService
                .addBooking(bookingOne, userTestMap.get(2L).getId(), itemTestMap.get(1L).getId());

        bookingService.updateBooking(bookingOne.getId(), itemTestMap.get(1L).getUserId(),
                false);
        Booking booking = bookingService.getBookingById(bookingOne.getId());

        Assertions.assertEquals(Status.REJECTED, booking.getStatus());
    }

    @Test
    void getBookingByIdShouldReturnBookingForBookerOrOwner() {
        bookingService
                .addBooking(bookingOne, userTestMap.get(2L).getId(), itemTestMap.get(1L).getId());

        Booking bookingOwner = bookingService
                .getBookingById(bookingOne.getId(), bookingOne.getItem().getUserId());
        Booking bookingBooker = bookingService.getBookingById(bookingOne.getId(),
                bookingOne.getBooker().getId());

        Assertions.assertEquals(bookingOne.getId(), bookingOwner.getId());
        Assertions.assertEquals(bookingOne.getItem().getId(), bookingOwner.getItem().getId());
        Assertions.assertEquals(bookingOne.getBooker().getId(), bookingOwner.getBooker().getId());
        Assertions.assertEquals(bookingOne.getId(), bookingBooker.getId());
        Assertions.assertEquals(bookingOne.getItem().getId(), bookingBooker.getItem().getId());
        Assertions.assertEquals(bookingOne.getBooker().getId(), bookingBooker.getBooker().getId());
    }

    /*@Test
    void getAllBookingOfUserWithStateAllShouldReturnListOfBookings() {
        bookingService.addBooking(bookingOne, bookingOne.getBooker().getId(),
                bookingOne.getItem().getId());
        bookingService.addBooking(bookingTwo, bookingTwo.getBooker().getId(),
                bookingTwo.getItem().getId());
        bookingService.addBooking(bookingThree, bookingThree.getBooker().getId(),
                bookingThree.getItem().getId());
        bookingService.addBooking(bookingFour, bookingFour.getBooker().getId(),
                bookingFour.getItem().getId());
        bookingService.addBooking(bookingFive, bookingFive.getBooker().getId(),
                bookingFive.getItem().getId());

        List<Booking> bookings = bookingService
                .getAllBookingOfUserWithState(userTestMap.get(3L).getId(), "ALL", 0, 10);

        Assertions.assertEquals(4, bookings.size());
    }*/

    @Test
    void getAllBookingOfUserWithStateAllShouldReturnListOfBookings() {
        bookingService.addBooking(bookingOne, bookingOne.getBooker().getId(),
                bookingOne.getItem().getId());
        bookingService.addBooking(bookingTwo, bookingTwo.getBooker().getId(),
                bookingTwo.getItem().getId());
        bookingService.addBooking(bookingThree, bookingThree.getBooker().getId(),
                bookingThree.getItem().getId());
        bookingService.addBooking(bookingFour, bookingFour.getBooker().getId(),
                bookingFour.getItem().getId());
        bookingService.addBooking(bookingFive, bookingFive.getBooker().getId(),
                bookingFive.getItem().getId());

        List<Booking> bookings = bookingService
                .getAllBookingOfUserWithState(userTestMap.get(3L).getId(), "ALL", 0, 10);

        Assertions.assertEquals(4, bookings.size());

        Booking firstBooking = bookings.get(0);
        Assertions.assertEquals(bookingOne.getId(), 4);
        Assertions.assertEquals(bookingOne.getItem().getId(), firstBooking.getItem().getId());
        Assertions.assertEquals(bookingOne.getBooker().getId(), firstBooking.getBooker().getId());

        Booking secondBooking = bookings.get(1);
        Assertions.assertEquals(bookingTwo.getId(), secondBooking.getId());
        Assertions.assertEquals(bookingTwo.getItem().getId(), secondBooking.getItem().getId());
        Assertions.assertEquals(bookingTwo.getBooker().getId(), secondBooking.getBooker().getId());
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateAllShouldReturnListOfBookings() {
        bookingService.addBooking(bookingOne, bookingOne.getBooker().getId(),
                bookingOne.getItem().getId());
        bookingService.addBooking(bookingTwo, bookingTwo.getBooker().getId(),
                bookingTwo.getItem().getId());
        bookingService.addBooking(bookingThree, bookingThree.getBooker().getId(),
                bookingThree.getItem().getId());
        bookingService.addBooking(bookingFour, bookingFour.getBooker().getId(),
                bookingFour.getItem().getId());
        bookingService.addBooking(bookingFive, bookingFive.getBooker().getId(),
                bookingFive.getItem().getId());

        List<Booking> bookings = bookingService
                .getAllBookingForItemsOfOwnerWithState(userTestMap.get(2L).getId(), "ALL",
                        0, 10);

        Assertions.assertEquals(2, bookings.size());
    }

    @Test
    void getItemLastBookingShouldReturnBookingWithPastDate() {
        bookingService.addBooking(bookingFive, bookingFive.getBooker().getId(),
                bookingFive.getItem().getId());

        Booking booking = bookingService.getItemLastBooking(bookingFive.getItem().getId());

        Assertions.assertEquals(1, booking.getId());
        Assertions.assertEquals(bookingFive.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void getItemNextBookingShouldReturnBookingWithPastDate() {
        bookingService.addBooking(bookingFour, bookingFour.getBooker().getId(),
                bookingFour.getItem().getId());

        Booking booking = bookingService.getItemNextBooking(bookingFour.getItem().getId());

        Assertions.assertEquals(1, booking.getId());
        Assertions.assertEquals(bookingFour.getItem().getId(), booking.getItem().getId());
    }

}
