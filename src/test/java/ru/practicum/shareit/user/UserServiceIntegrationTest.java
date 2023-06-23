package ru.practicum.shareit.user;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    private Map<Long, User> userTestMap;

    @BeforeEach
    void setUp() {
        userTestMap = Map.of(
            1L, new User(1, "testUserOne", "testUserOne@yandex.ru"),
            2L, new User(2, "testUserTwo", "testUserTwo@yandex.ru"),
            3L, new User(3, "testUserThree", "testUserThree@yandex.ru")
        );
    }

    @Test
    void addUserShouldWriteToDbAndReturn() {
        userService.addUser(userTestMap.get(1L));

        User user = userService.getUserById(userTestMap.get(1L).getId());

        Assertions.assertEquals(userTestMap.get(1L).getId(), user.getId());
        Assertions.assertEquals(userTestMap.get(1L).getName(), user.getName());
        Assertions.assertEquals(userTestMap.get(1L).getEmail(), user.getEmail());
    }

    @Test
    void updateUserShouldWriteToDbAndReturnUpdated() {
        userService.addUser(userTestMap.get(1L));
        userTestMap.get(1L).setName("Updated");

        userService.updateUser(userTestMap.get(1L));
        User user = userService.getUserById(userTestMap.get(1L).getId());

        Assertions.assertEquals(userTestMap.get(1L).getId(), user.getId());
        Assertions.assertEquals("Updated", user.getName());
        Assertions.assertEquals(userTestMap.get(1L).getEmail(), user.getEmail());
    }

    @Test
    void getUserByIdShouldReturnUser() {
        userService.addUser(userTestMap.get(1L));

        User user = userService.getUserById(userTestMap.get(1L).getId());

        Assertions.assertEquals(userTestMap.get(1L).getId(), user.getId());
        Assertions.assertEquals(userTestMap.get(1L).getName(), user.getName());
        Assertions.assertEquals(userTestMap.get(1L).getEmail(), user.getEmail());
    }

    @Test
    void getAllUsersShouldReturnListOfUsers() {
        userService.addUser(userTestMap.get(1L));
        userService.addUser(userTestMap.get(2L));
        userService.addUser(userTestMap.get(3L));

        List<User> users = userService.getAllUsers();

        Assertions.assertEquals(3, users.size());
    }

    @Test
    void deleteUserByIdShouldDeleteUser() {
        userService.addUser(userTestMap.get(1L));
        userService.addUser(userTestMap.get(2L));
        userService.addUser(userTestMap.get(3L));

        userService.deleteUserById(2L);
        List<User> users = userService.getAllUsers();

        Assertions.assertEquals(2, users.size());
    }

    @Test
    void isUserExistsShouldReturnTrue() {
        userService.addUser(userTestMap.get(1L));

        Assertions.assertTrue(userService.isUserExists(userTestMap.get(1L).getId()));
    }

    @Test
    void isUserExistsShouldReturnFalse() {
        userService.addUser(userTestMap.get(1L));

        Assertions.assertFalse(userService.isUserExists(userTestMap.get(2L).getId()));
    }

}
