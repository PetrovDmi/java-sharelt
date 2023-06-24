package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryJpaTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    List<User> testUsersList = List.of(
        User.builder().name("TestUserOne").email("testuserone@yandex.ru").build(),
        User.builder().name("TestUserTwo").email("testusertwo@yandex.ru").build(),
        User.builder().name("TestUserThree").email("testuserthree@yandex.ru").build()
    );

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void saveMethodTestShouldSaveAndReturnUser() {
        User user = userRepository.save(testUsersList.get(0));
        Assertions.assertNotNull(user);
    }

    @Test
    void getMethodTestShouldReturnUser() {
        userRepository.save(testUsersList.get(0));

        Optional<User> user = userRepository.findById(1L);

        Assertions.assertNotNull(user.get());
    }

    @Test
    void getAllMethodTestShouldReturnListOfUsers() {
        userRepository.save(testUsersList.get(0));
        userRepository.save(testUsersList.get(1));
        userRepository.save(testUsersList.get(2));

        List<User> users = userRepository.findAll();

        Assertions.assertNotNull(users);
        Assertions.assertEquals(3, users.size());
    }

}
