package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryJpaTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private PageRequest page = PageRequest.of(0, 10);

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
    void setUp() {
        userRepository.save(userTestMap.get(1L));
        userRepository.save(userTestMap.get(2L));
        userRepository.save(userTestMap.get(3L));

        itemRepository.save(itemTestMap.get(1L));
        itemRepository.save(itemTestMap.get(2L));
        itemRepository.save(itemTestMap.get(3L));
        itemRepository.save(itemTestMap.get(4L));
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void saveAndGetTestShouldReturnSavedBooking() {
        bookingRepository.save(bookingTestMap.get(1L));

        Optional<Booking> booking = bookingRepository.findById(1L);

        Assertions.assertNotNull(booking.get());
        Assertions.assertEquals(1, booking.get().getId());
    }

    @Test
    void findAllByBookerIdOrderByBookingStartDescTestShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdOrderByBookingStartDesc(userTestMap.get(2L).getId(), page);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(bookingTestMap.get(1L).getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(bookingTestMap.get(1L).getItem().getId());
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(bookingTestMap.get(1L).getBooker().getId());
        assertThat(bookings.get(0).getStatus()).isEqualTo(bookingTestMap.get(1L).getStatus());
        assertThat(bookings.get(0).getBookingStart()).isEqualTo(bookingTestMap.get(1L).getBookingStart());
        assertThat(bookings.get(0).getBookingEnd()).isEqualTo(bookingTestMap.get(1L).getBookingEnd());
    }

    @Test
    void findAllByBookerIdAndBookingStartIsAfterTestShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndBookingStartIsAfterOrderByBookingStartDesc(
                        userTestMap.get(3L).getId(), LocalDateTime.now().minusMinutes(10), page);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getId()).isEqualTo(bookingTestMap.get(4L).getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(bookingTestMap.get(4L).getItem().getId());
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(bookingTestMap.get(4L).getBooker().getId());
        assertThat(bookings.get(0).getStatus()).isEqualTo(bookingTestMap.get(4L).getStatus());
        assertThat(bookings.get(0).getBookingStart()).isEqualTo(bookingTestMap.get(4L).getBookingStart());
        assertThat(bookings.get(0).getBookingEnd()).isEqualTo(bookingTestMap.get(4L).getBookingEnd());
        assertThat(bookings.get(1).getId()).isEqualTo(bookingTestMap.get(5L).getId());
        assertThat(bookings.get(1).getItem().getId()).isEqualTo(bookingTestMap.get(5L).getItem().getId());
        assertThat(bookings.get(1).getBooker().getId()).isEqualTo(bookingTestMap.get(5L).getBooker().getId());
        assertThat(bookings.get(1).getStatus()).isEqualTo(bookingTestMap.get(5L).getStatus());
        assertThat(bookings.get(1).getBookingStart()).isEqualTo(bookingTestMap.get(5L).getBookingStart());
        assertThat(bookings.get(1).getBookingEnd()).isEqualTo(bookingTestMap.get(5L).getBookingEnd());
    }

    @Test
    void findAllByBookerIdAndBookingEndIsBeforeShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndBookingEndIsBeforeOrderByBookingStartDesc(
                        userTestMap.get(3L).getId(), LocalDateTime.now().minusMinutes(10), page);

        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0)).isEqualTo(bookingTestMap.get(2L));
        assertThat(bookings.get(1)).isEqualTo(bookingTestMap.get(3L));
    }

    @Test
    void findAllByBookerIdAndStatusOrderByBookingStartDescShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStatusOrderByBookingStartDesc(
                        userTestMap.get(3L).getId(), Status.WAITING, page);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getId()).isEqualTo(bookingTestMap.get(5L).getId());
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(bookingTestMap.get(5L).getItem().getId());
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(bookingTestMap.get(5L).getBooker().getId());
        assertThat(bookings.get(0).getStatus()).isEqualTo(bookingTestMap.get(5L).getStatus());
        assertThat(bookings.get(0).getBookingStart()).isEqualTo(bookingTestMap.get(5L).getBookingStart());
        assertThat(bookings.get(0).getBookingEnd()).isEqualTo(bookingTestMap.get(5L).getBookingEnd());
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
                .findAllByBookerIdAndStartBeforeAndEndAfter(
                        userTestMap.get(2L).getId(), LocalDateTime.now().minusMinutes(1), page);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0)).isEqualTo(bookingTestMap.get(1L));
        assertThat(bookings.get(0).getItem()).isEqualTo(bookingTestMap.get(1L).getItem());
        assertThat(bookings.get(0).getBooker()).isEqualTo(bookingTestMap.get(1L).getBooker());
        assertThat(bookings.get(0).getStatus()).isEqualTo(bookingTestMap.get(1L).getStatus());
        assertThat(bookings.get(0).getBookingStart()).isEqualTo(bookingTestMap.get(1L).getBookingStart());
        assertThat(bookings.get(0).getBookingEnd()).isEqualTo(bookingTestMap.get(1L).getBookingEnd());
    }

    @Test
    void testFindAllByOwnerIdAndStartAfterAndEndBeforeShouldReturnListOfBookings() {
        bookingRepository.saveAll(bookingTestMap.values());

        LocalDateTime start = LocalDateTime.now().minusMinutes(1);
        LocalDateTime end = LocalDateTime.now().plusHours(1);
        List<Booking> result = bookingRepository.findAllByOwnerIdAndStartAfterAndEndBefore(
                1L, start, page);

        Assertions.assertEquals(1, result.size());
        Booking expectedBooking = bookingTestMap.get(6L);
    }

    @Test
    void findAllByOwnerIdAndStartAfterShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
            .findAllByOwnerIdAndStartAfter(2L, LocalDateTime.now().minusMinutes(1), page);

        Assertions.assertEquals(2, bookings.size());
    }

    @Test
    void findAllByOwnerIdAndEndBeforeShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
            .findAllByOwnerIdAndEndBefore(2L, LocalDateTime.now().minusMinutes(1), page);

        Assertions.assertEquals(2, bookings.size());
    }

    @Test
    void findAllByOwnerIdAndStateShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
            .findAllByOwnerIdAndState(1L, Status.WAITING, page);

        Assertions.assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemAndStateFutureShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
            .findAllByItemAndStateFuture(3L, LocalDateTime.now().minusMinutes(1),
                Status.WAITING);

        Assertions.assertEquals(1, bookings.size());
    }

    @Test
    void findAllByItemAndStatePastShouldReturnListOfBookings() {
        bookingRepository.save(bookingTestMap.get(1L));
        bookingRepository.save(bookingTestMap.get(2L));
        bookingRepository.save(bookingTestMap.get(3L));
        bookingRepository.save(bookingTestMap.get(4L));
        bookingRepository.save(bookingTestMap.get(5L));
        bookingRepository.save(bookingTestMap.get(6L));

        List<Booking> bookings = bookingRepository
            .findAllByItemAndStatePast(3L, LocalDateTime.now().minusMinutes(1),
                Status.REJECTED);

        Assertions.assertEquals(1, bookings.size());
    }
}
