package ru.practicum.shareit.user.controller;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Component
public class UserMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public UserDto convertToDto(User user) {
        UserDto userDto = modelMapper.map(user, UserDto.class);
        return userDto;
    }

    public User convertToEntity(UserService userService, UserDto userDto, Long userId) {
        User user = modelMapper.map(userDto, User.class);
        if (userId != 0) {
            user.setId(userId);
        }
        User oldUser;

        if (userService.isUserExists(userId)) {
            oldUser = userService.getUserById(userId);
            if (user.getEmail() == null) {
                user.setEmail(oldUser.getEmail());
            }
            if (user.getName() == null) {
                user.setName(oldUser.getName());
            }

        }
        return user;
    }
}
