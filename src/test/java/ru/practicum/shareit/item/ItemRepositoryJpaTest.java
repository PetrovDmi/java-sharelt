package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRepositoryJpaTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
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

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @BeforeEach
    void setUp() {
        List<User> users = new ArrayList<>(userTestMap.values());
        List<Item> items = new ArrayList<>(itemTestMap.values());

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

    }

    @Test
    void findAllByUserIdOrderByIdShouldReturnListOfItems() {
        List<Item> items = itemRepository.findAllByUserIdOrderById(2L);

        Assertions.assertEquals(3, items.size());
    }

    @Test
    void findByNameOrDescriptionLikeShouldReturnListOfItems() {
        List<Item> items = itemRepository.findByNameOrDescriptionLike("ал", page);

        Assertions.assertEquals(2, items.size());
    }

    @Test
    void findAllByRequestIdOrderByIdShouldReturnListOfItems() {
        List<Item> items = itemRepository.findAllByRequestIdOrderById(1, page);

        Assertions.assertEquals(0, items.size());
    }
}
