package ru.practicum.shareit.item;

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
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CommentRepositoryJpaTest {

    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;

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

    private final Map<Long, Comment> commentTestMap = Map.of(
        1L, new Comment(1L, "All Good", itemTestMap.get(1L), userTestMap.get(2L), created),
        2L, new Comment(2L, "All Fine", itemTestMap.get(2L), userTestMap.get(2L), created),
        3L, new Comment(2L, "All Fine", itemTestMap.get(1L), userTestMap.get(3L), created)
    );

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @BeforeEach
    void setUp() {
        List<User> users = new ArrayList<>(userTestMap.values());
        List<Item> items = new ArrayList<>(itemTestMap.values());
        List<Comment> comments = new ArrayList<>(commentTestMap.values());

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

        for (Comment comment : comments) {
            em.persist(Comment.builder().text(comment.getText())
                .author(comment.getAuthor())
                .created(comment.getCreated())
                .item(comment.getItem())
                .build());
        }
        em.flush();
    }

    @Test
    void findAllByItemIdShouldReturnListOfComments() {
        List<Comment> comments = commentRepository.findAllByItemId(1L);

        Assertions.assertEquals(2, comments.size());
    }

}
