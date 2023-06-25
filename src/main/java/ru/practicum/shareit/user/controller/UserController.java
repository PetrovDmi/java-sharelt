package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("Получен запрос на добавление пользователя");
        User user = mapper.convertToEntity(userService, userDto, 0L);
        userService.addUser(user);
        return mapper.convertToDto(userService.getUserById(user.getId()));
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable Long userId) {
        log.debug("Получен запрос на обновление пользователя");
        User user = mapper.convertToEntity(userService, userDto, userId);
        userService.updateUser(user);
        return mapper.convertToDto(userService.getUserById(user.getId()));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("Получен запрос на получение всех пользователей");
        ArrayList<User> users = new ArrayList<>(userService.getAllUsers());
        ArrayList<UserDto> userDtos = new ArrayList<>();
        for (User user : users) {
            userDtos.add(mapper.convertToDto(user));
        }
        return userDtos;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.debug("Получен запрос на получение пользователя по id");
        return mapper.convertToDto(userService.getUserById(userId));
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        log.debug("Получен запрос на удаление пользователя по id");
        userService.deleteUserById(userId);
    }
}
