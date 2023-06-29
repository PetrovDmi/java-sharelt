package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class ItemServiceUnitTests {

    private ItemService itemService;
    private ItemRepository mockItemRepository;
    private CommentRepository mockCommentRepository;
    private UserService mockUserService;
    private BookingRepository mockBookingRepository;
    private UserRepository mockUserRepository;

    private final PageRequest page = PageRequest.of(0, 10);
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

    Comment comment = new Comment(1, "All Good", itemTestMap.get(1L),
            userTestMap.get(2L), created);
    Comment commentBad = new Comment(1, "All Good", itemTestMap.get(2L),
            userTestMap.get(3L), created);

    Booking booking = new Booking(1, itemTestMap.get(1L), userTestMap.get(2L), Status.APPROVED,
            created.minusHours(4), created.minusHours(1));

    @BeforeEach
    void setUp() {
        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockBookingRepository = Mockito.mock(BookingRepository.class);
        mockCommentRepository = Mockito.mock(CommentRepository.class);
        mockUserService = Mockito.mock(UserService.class);
        mockUserRepository = Mockito.mock(UserRepository.class);

        itemService = new ItemServiceImpl(mockItemRepository, mockCommentRepository,
                mockUserRepository,
                mockBookingRepository);

        Mockito.when(mockItemRepository.findById(5L)).thenReturn(Optional.empty());
        Mockito.when(mockItemRepository.findById(1L)).thenReturn(
                Optional.ofNullable(itemTestMap.get(1L)));
        Mockito.when(mockItemRepository.findById(2L)).thenReturn(
                Optional.ofNullable(itemTestMap.get(2L)));
        Mockito.when(mockItemRepository.findById(3L)).thenReturn(
                Optional.ofNullable(itemTestMap.get(3L)));
        Mockito.when(mockItemRepository.findById(4L)).thenReturn(
                Optional.ofNullable(itemTestMap.get(4L)));
        Mockito.when(mockItemRepository.findById(5L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        Mockito.when(mockItemRepository.findByNameOrDescriptionLike("1", page))
                .thenReturn(List.of(itemTestMap.get(1L), itemTestMap.get(2L), itemTestMap.get(3L),
                        itemTestMap.get(4L)));

        Mockito.when(mockItemRepository.findAllByUserIdOrderById(1L))
                .thenReturn(List.of(itemTestMap.get(1L)));

        Mockito.when(mockCommentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        Mockito.when(mockBookingRepository.findAllByBookerIdOrderByBookingStartDesc(2, page))
                .thenReturn(List.of(booking));
        Mockito.when(mockBookingRepository.findAllByBookerIdOrderByBookingStartDesc(3, page))
                .thenReturn(List.of(booking));

        Mockito.when(mockItemRepository.findAllByRequestIdOrderById(1L, page))
                .thenReturn(List.of(itemTestMap.get(1L)));

        Mockito.when(mockUserService.isUserExists(1L)).thenReturn(true);
        Mockito.when(mockUserService.isUserExists(2L)).thenReturn(true);
        Mockito.when(mockUserService.isUserExists(3L)).thenReturn(true);
        Mockito.when(mockUserService.isUserExists(4L)).thenReturn(false);
    }

    @Test
    void addItemShouldCallRepositorySaveMethod() {
        itemService.addItem(itemTestMap.get(1L));

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .save(itemTestMap.get(1L));
    }

    @Test
    void getItemShouldThrowNotFoundExceptionUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.getItem(5L)
        );
        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getItemShouldReturnAnItem() {
        Item item = itemService.getItem(1L);

        Assertions.assertEquals(itemTestMap.get(1L).getId(), item.getId());
        Assertions.assertEquals(itemTestMap.get(1L).getUserId(), item.getUserId());
        Assertions.assertEquals(itemTestMap.get(1L).getAvailable(), item.getAvailable());
        Assertions.assertEquals(itemTestMap.get(1L).getDescription(), item.getDescription());
        Assertions.assertEquals(itemTestMap.get(1L).getName(), item.getName());
    }

    @Test
    void deleteItemByIdShouldDeleteAnItem() {
        itemService.deleteItemById(1L);

        Mockito.verify(mockItemRepository, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    void getItemsByNameOrDescriptionSearchShouldReturnListOfItems() {
        List<Item> items = itemService.getItemsByNameOrDescriptionSearch("1", 0, 10);

        Assertions.assertEquals(4, items.size());
    }

    @Test
    void getItemsByNameOrDescriptionBlankSearchShouldReturnEmptyList() {
        List<Item> items = itemService.getItemsByNameOrDescriptionSearch("", 0, 10);

        Assertions.assertEquals(0, items.size());
    }

    @Test
    void getUserItemsShouldReturnListOfItems() {
        List<Item> items = itemService.getUserItems(1L, 0, 10);

        Assertions.assertEquals(1, items.size());
    }

    @Test
    void isItemExistsShouldReturnTrue() {
        boolean flag = itemService.isItemExists(2L);

        Assertions.assertTrue(flag);
    }

    @Test
    void isItemExistsShouldReturnFalse() {
        boolean flag = itemService.isItemExists(5L);

        Assertions.assertFalse(flag);
    }

    @Test
    void isItemAvailableShouldReturnTrue() {
        boolean flag = itemService.isItemAvailable(2L);

        Assertions.assertTrue(flag);
    }

    @Test
    void isItemAvailableShouldReturnFalse() {
        boolean flag = itemService.isItemAvailable(4L);

        Assertions.assertFalse(flag);
    }

    @Test
    void isUserAnItemOwnerShouldReturnTrue() {
        boolean flag = itemService.isUserAnItemOwner(1L, itemTestMap.get(1L));

        Assertions.assertTrue(flag);
    }

    @Test
    void isUserAnItemOwnerShouldReturnFalse() {
        boolean flag = itemService.isUserAnItemOwner(1L, itemTestMap.get(3L));

        Assertions.assertFalse(flag);
    }

    @Test
    void isUserAnItemsOwnerEmptyListShouldReturnFalse() {
        boolean flag = itemService.isUserAnItemsOwner(1L, new ArrayList<>());

        Assertions.assertFalse(flag);
    }

    @Test
    void isUserAnItemsOwnerShouldReturnFalse() {
        boolean flag = itemService.isUserAnItemsOwner(1L, List.of(itemTestMap.get(3L),
                itemTestMap.get(2L)));

        Assertions.assertFalse(flag);
    }

    @Test
    void isUserAnItemsOwnerShouldReturnTrue() {
        boolean flag = itemService.isUserAnItemsOwner(2L, List.of(itemTestMap.get(3L),
                itemTestMap.get(2L)));

        Assertions.assertTrue(flag);
    }

    @Test
    void addCommentToItemShouldAddComment() {
        itemService.addCommentToItem(comment);
        Mockito.verify(mockCommentRepository, Mockito.times(1))
                .save(comment);
    }

    @Test
    void addCommentToItemNotGottenShouldThrowValidationException() {
        final ValidationException exception = Assertions.assertThrows(ValidationException.class,
                () -> itemService.addCommentToItem(commentBad)
        );
        Assertions.assertEquals("Комментировать могут только бронировавшие вещь "
                + "пользователи", exception.getMessage());
    }

    @Test
    void getCommentByIdToItemShouldReturnComment() {
        Comment result = itemService.getCommentById(1L);

        Assertions.assertEquals(comment.getId(), result.getId());
        Assertions.assertEquals(comment.getAuthor().getId(), result.getAuthor().getId());
        Assertions.assertEquals(comment.getCreated(), result.getCreated());
        Assertions.assertEquals(comment.getItem().getId(), result.getItem().getId());
        Assertions.assertEquals(comment.getText(), result.getText());
    }

    @Test
    void getItemsByRequestIdShouldReturnItemRequest() {
        List<Item> items = itemService.getItemsByRequestId(1L);

        Assertions.assertEquals(1, items.size());
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findAllByRequestIdOrderById(1L, page);
    }

    @Test
    void isUserExistsOrException_UserDoesNotExist_ShouldThrowNotFoundException() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            itemService.isUserExistsOrException(10L);
        });
    }

    @Test
    void isUserExistsOrExceptionUserNotExistsShouldThrowNotFoundException() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> itemService.isUserExistsOrException(4L)
        );

        Assertions.assertEquals("Пользователь не был найден", exception.getMessage());
    }
}
