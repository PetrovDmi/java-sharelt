package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addUser(User user) {
        log.info("Создание нового пользователя");
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        log.info("Изменение пользователя с id " + user.getId());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(long userId) {
        log.info("Получение пользователя с id " + userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не был найден");
        }
        return optionalUser.get();
    }

    @Override
    @Transactional
    public void deleteUserById(long userId) {
        log.info("Удаление пользователя с id " + userId);
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserExists(long userId) {
        log.info("Запрос существования пользователя с id " + userId);
        try {
            getUserById(userId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }


}
