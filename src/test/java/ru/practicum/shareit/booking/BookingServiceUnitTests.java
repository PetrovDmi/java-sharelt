package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class BookingServiceUnitTests {

    private BookingService bookingService;
    private BookingRepository mockBookingRepository;
    private ItemRepository mockItemRepository;
    private UserRepository mockUserRepository;
    private final PageRequest page = PageRequest.of(0, 10);

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

    private final Map<Long, Booking> bookingTestMap = Map.of(
            1L, new Booking(1, itemTestMap.get(1L), userTestMap.get(2L), Status.WAITING,
                    LocalDateTime.now().minusMinutes(30), LocalDateTime.now().plusHours(2)),
            2L, new Booking(2, itemTestMap.get(3L), userTestMap.get(3L), Status.APPROVED,
                    LocalDateTime.now().minusMinutes(45), LocalDateTime.now().minusMinutes(15)),
            3L, new Booking(3, itemTestMap.get(2L), userTestMap.get(3L), Status.REJECTED,
                    LocalDateTime.now().minusMinutes(45), LocalDateTime.now().minusHours(4)),
            4L, new Booking(4, itemTestMap.get(3L), userTestMap.get(3L), Status.APPROVED,
                    LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(4)),
            5L, new Booking(5, itemTestMap.get(4L), userTestMap.get(3L), Status.WAITING,
                    LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(4)),
            6L, new Booking(6, itemTestMap.get(1L), userTestMap.get(1L), Status.WAITING,
                    LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(4))
    );

    @BeforeEach
    public void setUp() {
        mockBookingRepository = Mockito.mock(BookingRepository.class);
        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockUserRepository = Mockito.mock(UserRepository.class);

        Mockito.when(mockUserRepository.findById(3L)).thenReturn(Optional.ofNullable(userTestMap.get(3L)));
        Mockito.when(mockUserRepository.findById(2L)).thenReturn(Optional.ofNullable(userTestMap.get(2L)));
        Mockito.when(mockUserRepository.findById(1L)).thenReturn(Optional.ofNullable(userTestMap.get(1L)));

        Mockito.when(mockItemRepository.findById(1L)).thenReturn(Optional.ofNullable(itemTestMap.get(1L)));
        Mockito.when(mockItemRepository.findById(2L)).thenReturn(Optional.ofNullable(itemTestMap.get(2L)));
        Mockito.when(mockItemRepository.findById(3L)).thenReturn(Optional.ofNullable(itemTestMap.get(3L)));
        Mockito.when(mockItemRepository.findById(4L)).thenReturn(Optional.ofNullable(itemTestMap.get(4L)));

        Mockito.when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.ofNullable(bookingTestMap.get(1L)));
        Mockito.when(mockBookingRepository.findById(2L))
                .thenReturn(Optional.ofNullable(bookingTestMap.get(2L)));
        Mockito.when(mockBookingRepository.findAll())
                .thenReturn(new ArrayList<>(bookingTestMap.values()));

        bookingService = new BookingServiceImpl(mockBookingRepository, mockItemRepository,
                mockUserRepository);
    }

    @AfterEach
    void clearChangeInSet() {
        bookingTestMap.get(1L).setStatus(Status.WAITING);
    }

    @Test
    void addBookingAvailableFalseShouldThrowValidationException() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.addBooking(bookingTestMap.get(5L),
                        bookingTestMap.get(5L).getBooker().getId(),
                        bookingTestMap.get(5L).getItem().getId()));
        Assertions.assertEquals("Вещь недоступна к бронированию", exception.getMessage());
    }

    @Test
    void addBookingByOwnerShouldThrowNotFoundException() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(bookingTestMap.get(6L),
                        bookingTestMap.get(6L).getBooker().getId(),
                        bookingTestMap.get(6L).getItem().getId()));
        Assertions.assertEquals("Владелец не может бронировать собственную вещь",
                exception.getMessage());
    }

    @Test
    void addBookingShouldCallRepositorySaveMethod() {
        bookingService
                .addBooking(bookingTestMap.get(1L), bookingTestMap.get(1L).getBooker().getId(),
                        bookingTestMap.get(1L).getItem().getId());
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .save(bookingTestMap.get(1L));
    }

    @Test
    void updateBookingNotAnOwnerShouldThrowNotFoundException() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.updateBooking(bookingTestMap.get(1L).getId(),
                        userTestMap.get(2L).getId(), true));
        Assertions.assertEquals("Пользователь не является владельцем!",
                exception.getMessage());
    }

    @Test
    void updateBookingShouldThrowValidationException() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> bookingService.updateBooking(bookingTestMap.get(2L).getId(),
                        bookingTestMap.get(2L).getItem().getUserId(), true));
        Assertions.assertEquals("Нельзя сменить статус из другого статуса",
                exception.getMessage());
    }

    @Test
    void updateBookingShouldCallRepositorySaveMethod() {
        bookingService.updateBooking(bookingTestMap.get(1L).getId(),
                bookingTestMap.get(1L).getItem().getUserId(), true);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .save(bookingTestMap.get(1L));
    }

    @Test
    void getBookingByIdShouldThrowNotFoundException() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService
                        .getBookingById(bookingTestMap.get(1L).getId(), userTestMap.get(3L).getId()));
        Assertions.assertEquals("Доступ к предмету ограничен хозяином или резерватором",
                exception.getMessage());
    }

    @Test
    void getBookingByIdShouldCallRepositoryFindMethod() {
        bookingService.getBookingById(bookingTestMap.get(1L).getId(),
                bookingTestMap.get(1L).getBooker().getId());
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findById(bookingTestMap.get(1L).getId());
    }

    @Test
    void getAllBookingOfUserWithStateUserNotExistShouldThrowNotFoundException() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingOfUserWithState(5L, "ALL", 0, 10));
        Assertions.assertEquals("Пользователь с id 5 не найден", exception.getMessage());
    }

    @Test
    void getAllBookingOfUserWithStateAllUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingOfUserWithState(userTestMap.get(1L).getId(), "ALL",
                0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBookerIdOrderByBookingStartDesc(userTestMap.get(1L).getId(), page);
    }

    @Test
    void getAllBookingOfUserWithStatePastUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingOfUserWithState(userTestMap.get(1L).getId(), "PAST",
                0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBookerIdAndBookingEndIsBeforeOrderByBookingStartDesc(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingOfUserWithStateFutureUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingOfUserWithState(userTestMap.get(1L).getId(), "FUTURE",
                0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBookerIdAndBookingStartIsAfterOrderByBookingStartDesc(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingOfUserWithStateCurrentUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingOfUserWithState(userTestMap.get(1L).getId(), "CURRENT",
                0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfter(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingOfUserWithStateWaitingUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingOfUserWithState(userTestMap.get(1L).getId(), "WAITING",
                0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatusOrderByBookingStartDesc(userTestMap.get(1L).getId(),
                        Status.WAITING, page);
    }

    @Test
    void getAllBookingOfUserWithStateRejectedUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingOfUserWithState(userTestMap.get(1L).getId(), "REJECTED",
                0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByBookerIdAndStatusOrderByBookingStartDesc(userTestMap.get(1L).getId(),
                        Status.REJECTED, page);
    }

    @Test
    void getAllBookingOfUserWithStateUnknownShouldThrowRuntimeException() {
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.getAllBookingOfUserWithState(userTestMap.get(1L).getId(),
                        "Hello", 0, 10));
        Assertions.assertEquals("Unknown state: Hello", exception.getMessage());
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateUserNotExistShouldThrowNotFoundException() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingForItemsOfOwnerWithState(5L, "ALL",
                        0, 10));
        Assertions.assertEquals("Пользователь с id 5 не найден", exception.getMessage());
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateUserNotOwnerShouldThrowNotFoundException() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getAllBookingForItemsOfOwnerWithState(3L, "ALL",
                        0, 10));
        Assertions.assertEquals("Не найдено предметов у пользователя",
                exception.getMessage());
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateAllUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingForItemsOfOwnerWithState(userTestMap.get(1L).getId(),
                "ALL", 0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByOwnerId(userTestMap.get(1L).getId(), page);
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStatePastUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingForItemsOfOwnerWithState(userTestMap.get(1L).getId(),
                "PAST", 0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByOwnerIdAndEndBefore(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateFutureUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingForItemsOfOwnerWithState(userTestMap.get(1L).getId(),
                "FUTURE", 0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByOwnerIdAndStartAfter(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateCurrentUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingForItemsOfOwnerWithState(userTestMap.get(1L).getId(),
                "CURRENT", 0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByOwnerIdAndStartAfterAndEndBefore(Mockito.anyLong(),
                        Mockito.any(LocalDateTime.class), Mockito.any(PageRequest.class));
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateWaitingUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingForItemsOfOwnerWithState(userTestMap.get(1L).getId(),
                "WAITING", 0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByOwnerIdAndState(userTestMap.get(1L).getId(), Status.WAITING, page);
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateRejectedUserShouldCallRepositoryGetMethod() {
        bookingService.getAllBookingForItemsOfOwnerWithState(userTestMap.get(1L).getId(),
                "REJECTED", 0, 10);
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByOwnerIdAndState(userTestMap.get(1L).getId(), Status.REJECTED, page);
    }

    @Test
    void getAllBookingForItemsOfOwnerWithStateUnknownShouldThrowRuntimeException() {
        final RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> bookingService.getAllBookingForItemsOfOwnerWithState(userTestMap.get(1L).getId(),
                        "Hello", 0, 10));
        Assertions.assertEquals("Unknown state: Hello", exception.getMessage());
    }

    @Test
    void getItemLastBookingShouldCallRepositoryGetMethod() {
        bookingService.getItemLastBooking(itemTestMap.get(1L).getId());
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByItemAndStatePast(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Status.class));
    }

    @Test
    void getItemNextBookingShouldCallRepositoryGetMethod() {
        bookingService.getItemNextBooking(itemTestMap.get(1L).getId());
        Mockito.verify(mockBookingRepository, Mockito.times(1))
                .findAllByItemAndStateFuture(Mockito.anyLong(), Mockito.any(LocalDateTime.class),
                        Mockito.any(Status.class));
    }

    public UserRepository getMockUserRepository() {
        return mockUserRepository;
    }

    public void setMockUserRepository(UserRepository mockUserRepository) {
        this.mockUserRepository = mockUserRepository;
    }
}
