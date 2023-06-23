package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestRepositoryJpaTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private RequestRepository repository;

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
        List<User> users = new ArrayList<>(userTestMap.values());
        List<Item> items = new ArrayList<>(itemTestMap.values());
        List<ItemRequest> itemRequests = new ArrayList<>(itemRequestTestMap.values());

        for (User user : users) {
            em.persist(User.builder().name(user.getName()).email(user.getEmail()).build());
        }
        em.flush();
        for (Item item : items) {
            em.persist(Item.builder().name(item.getName())
                .description(item.getDescription())
                .userId(item.getUserId())
                .available(item.getAvailable())
                .build());
        }
        em.flush();

        for (ItemRequest itemRequest : itemRequests) {
            em.persist(ItemRequest.builder().description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(itemRequest.getRequester())
                .build());
        }
    }

    @Test
    void findAllByRequesterIdOrderByIdDescShouldReturnListOfUserRequests() {
        List<ItemRequest> itemRequests = repository.findAllByRequesterIdOrderByIdDesc(1L);
        Assertions.assertEquals(2, itemRequests.size());
    }

    @Test
    void findByRequesterNotOrderByIdDescShouldReturnListOfNotUserRequests() {
        List<ItemRequest> itemRequests = repository
            .findByRequesterNotOrderByIdDesc(1L, page);
        Assertions.assertEquals(1, itemRequests.size());
    }


}
