package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    void addUser(User user);

    void updateUser(User user);

    List<User> getAllUsers();

    User getUserById(long userId);

    void deleteUserById(long userId);

    boolean isUserExists(long userId);
}
