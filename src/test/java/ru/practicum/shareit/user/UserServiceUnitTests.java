package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest

public class UserServiceUnitTests {
    private UserService userService;

    private UserRepository mockUserRepository;

    private final Map<Long, User> userTestMap = Map.of(
            1L, new User(1, "testUserOne", "testUserOne@yandex.ru"),
            2L, new User(2, "testUserTwo", "testUserTwo@yandex.ru"),
            3L, new User(3, "testUserThree", "testUserThree@yandex.ru")
    );

    private final User user6 = new User(6, "testUser5", "testUser5@yandex.ru");

    @BeforeEach
    void setUp() {
        mockUserRepository = Mockito.mock(UserRepository.class);
        userService = new UserServiceImpl(mockUserRepository);

        Mockito.when(mockUserRepository.findById(1L)).thenReturn(
                Optional.ofNullable(userTestMap.get(1L)));
        Mockito.when(mockUserRepository.findById(2L)).thenReturn(
                Optional.ofNullable(userTestMap.get(2L)));
        Mockito.when(mockUserRepository.findById(3L)).thenReturn(
                Optional.ofNullable(userTestMap.get(3L)));
        Mockito.when(mockUserRepository.findById(4L))
                .thenThrow(new NotFoundException("пользователь не найден"));
        Mockito.when(mockUserRepository.findAll())
                .thenReturn(new ArrayList<>(userTestMap.values()));
        Mockito.when(mockUserRepository.findById(6L)).thenReturn(Optional.empty());
    }

    /*@Test
    void addUserShouldCallRepositorySaveMethod() {
        userService.addUser(user6);
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .save(user6);
    }*/
    @Test
    void addUserShouldCallRepositorySaveMethod() {
        // Подготовка
        Mockito.when(mockUserRepository.findById(6L)).thenReturn(Optional.empty());

        // Выполнение
        userService.addUser(user6);

        // Проверка
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(user6);
    }

    /*@Test
    void updateUserShouldCallRepositorySaveMethod() {
        userService.updateUser(userTestMap.get(1L));
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .save(userTestMap.get(1L));
    }*/
    @Test
    void updateUserShouldCallRepositorySaveMethod() {
        // Подготовка
        User updatedUser = userTestMap.get(1L);
        Mockito.when(mockUserRepository.save(updatedUser)).thenReturn(updatedUser);

        // Выполнение
        userService.updateUser(updatedUser);

        // Проверка
        Mockito.verify(mockUserRepository, Mockito.times(1)).save(updatedUser);
    }

    @Test
    void deleteUserByIdShouldCallRepositoryDeleteMethod() {
        userService.deleteUserById(userTestMap.get(1L).getId());
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .deleteById(userTestMap.get(1L).getId());
    }

    @Test
    void isUserExistsShouldReturnTrue() {
        boolean flag = userService.isUserExists(1L);
        Assertions.assertTrue(flag);
    }

    @Test
    void isUserExistsShouldReturnFalse() {
        boolean flag = userService.isUserExists(4L);
        Assertions.assertFalse(flag);
    }

    @Test
    void getUserByIdShouldReturnUser() {
        User user = userService.getUserById(1L);
        Assertions.assertEquals(userTestMap.get(1L).getId(), user.getId());
        Assertions.assertEquals(userTestMap.get(1L).getName(), user.getName());
        Assertions.assertEquals(userTestMap.get(1L).getEmail(), user.getEmail());
        Mockito.verify(mockUserRepository, Mockito.times(1))
                .findById(userTestMap.get(1L).getId());
    }

    @Test
    void getAllUsersShouldReturnListOfUsers() {
        List<User> users = userService.getAllUsers();
        Assertions.assertEquals(3, users.size());
    }
}
