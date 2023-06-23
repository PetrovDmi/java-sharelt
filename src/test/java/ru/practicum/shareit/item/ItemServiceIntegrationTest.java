package ru.practicum.shareit.item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingRepository bookingRepository;

    private PageRequest page = PageRequest.of(0, 10);
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

    Booking booking = new Booking(1, itemTestMap.get(1L), userTestMap.get(2L), Status.APPROVED,
        created.minusHours(4), created.minusHours(1));

    @BeforeEach
    void setUp() {
        userService.addUser(userTestMap.get(1L));
        userService.addUser(userTestMap.get(2L));
        userService.addUser(userTestMap.get(3L));
        itemTestMap.get(1L).setDescription("Описание дрели");
    }

    @Test
    void addItemTestShouldWriteToDbAndReturn() {
        itemService.addItem(itemTestMap.get(1L));

        Item item = itemService.getItem(itemTestMap.get(1L).getId());

        Assertions.assertEquals(itemTestMap.get(1L).getId(), item.getId());
        Assertions.assertEquals(itemTestMap.get(1L).getName(), item.getName());
        Assertions.assertEquals(itemTestMap.get(1L).getDescription(), item.getDescription());
    }

    @Test
    void updateItemTestShouldWriteToDbAndReturnUpdated() {
        itemService.addItem(itemTestMap.get(1L));
        itemTestMap.get(1L).setDescription("Changed");

        itemService.updateItem(itemTestMap.get(1L));
        Item item = itemService.getItem(itemTestMap.get(1L).getId());

        Assertions.assertEquals(itemTestMap.get(1L).getId(), item.getId());
        Assertions.assertEquals(itemTestMap.get(1L).getName(), item.getName());
        Assertions.assertEquals("Changed", item.getDescription());
    }

    @Test
    void getItemShouldReturnAnItem() {
        itemService.addItem(itemTestMap.get(1L));

        Item item = itemService.getItem(itemTestMap.get(1L).getId());

        Assertions.assertEquals(itemTestMap.get(1L).getId(), item.getId());
        Assertions.assertEquals(itemTestMap.get(1L).getName(), item.getName());
        Assertions.assertEquals(itemTestMap.get(1L).getDescription(), item.getDescription());
    }

    @Test
    void deleteItemByIdShouldDeleteAnItem() {
        itemService.addItem(itemTestMap.get(1L));
        itemService.addItem(itemTestMap.get(2L));
        itemService.addItem(itemTestMap.get(3L));
        itemService.addItem(itemTestMap.get(4L));

        itemService.deleteItemById(2L);
        List<Item> items = itemService.getUserItems(2L, 0, 10);

        Assertions.assertEquals(2, items.size());
    }

    @Test
    void getItemsByNameOrDescriptionSearchShouldReturnListOfItems() {
        itemService.addItem(itemTestMap.get(1L));
        itemService.addItem(itemTestMap.get(2L));
        itemService.addItem(itemTestMap.get(3L));
        itemService.addItem(itemTestMap.get(4L));

        List<Item> items = itemService.getItemsByNameOrDescriptionSearch("Кув", 0, 10);

        Assertions.assertEquals(2, items.size());
    }

    @Test
    void addCommentToItemShouldAddAComment() {
        itemService.addItem(itemTestMap.get(1L));
        itemService.addItem(itemTestMap.get(2L));
        itemService.addItem(itemTestMap.get(3L));
        itemService.addItem(itemTestMap.get(4L));
        bookingRepository.save(booking);
        itemService.addCommentToItem(comment);

        Comment result = itemService.getCommentById(comment.getId());

        Assertions.assertEquals(comment.getId(), result.getId());
    }

    @Test
    void getCommentByIdToItemShouldAddAComment() {
        itemService.addItem(itemTestMap.get(1L));
        itemService.addItem(itemTestMap.get(2L));
        itemService.addItem(itemTestMap.get(3L));
        itemService.addItem(itemTestMap.get(4L));
        bookingRepository.save(booking);
        itemService.addCommentToItem(comment);

        Comment result = itemService.getCommentById(comment.getId());

        Assertions.assertEquals(comment.getId(), result.getId());
        Assertions.assertEquals(comment.getText(), result.getText());
        Assertions.assertEquals(comment.getCreated(), result.getCreated());
        Assertions.assertEquals(comment.getAuthor().getId(), result.getAuthor().getId());
        Assertions.assertEquals(comment.getItem().getId(), result.getItem().getId());
    }

    @Test
    void getUserItemsShouldReturnListOfItems() {
        itemService.addItem(itemTestMap.get(1L));
        itemService.addItem(itemTestMap.get(2L));
        itemService.addItem(itemTestMap.get(3L));
        itemService.addItem(itemTestMap.get(4L));

        List<Item> items = itemService.getUserItems(2L, 0, 10);

        Assertions.assertEquals(3, items.size());
    }


}
