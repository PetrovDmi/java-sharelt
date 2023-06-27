package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private static final int ITEM_LIST_PAGE_SIZE = 10;

    @Override
    @Transactional(readOnly = true)
    public void isUserExistsOrException(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не был найден");
        }
    }

    @Override
    @Transactional
    public void addItem(Item item) {
        log.info("Создание нового предмета");
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void updateItem(Item item) {
        log.info("Обновление предмета с id " + item.getId());
        itemRepository.save(item);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(long itemId) {
        log.info("Получение предмета с id " + itemId);
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return optionalItem.get();
    }

    @Override
    @Transactional
    public void deleteItemById(long itemId) {
        log.info("Получение предмета с id " + itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getItemsByNameOrDescriptionSearch(String text, int from, int size) {
        log.info("Получение списка всех предметов пользователя с фильтром " + text);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepository.findByNameOrDescriptionLike(text.toLowerCase(), page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> getUserItems(long userId, int from, int size) {
        log.info("Получение списка всех предметов пользователя с id " + userId);
        return itemRepository.findAllByUserIdOrderById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isItemExists(long itemId) {
        log.info("Проверка существовария предмета с id " + itemId);
        try {
            getItem(itemId);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public boolean isItemAvailable(long itemId) {
        isItemExists(itemId);
        return getItem(itemId).getAvailable();
    }

    @Override
    public boolean isUserAnItemOwner(long userId, Item item) {
        return item.getUserId() == userId;
    }

    @Override
    public boolean isUserAnItemsOwner(long userId, List<Item> items) {
        if (items.isEmpty()) {
            return false;
        }
        return items.get(0).getUserId() == userId;
    }

    @Override
    public void addCommentToItem(Comment comment) {
        log.info("Добавление комментария");
        if (!isUserWasAnItemBooker(comment.getAuthor().getId(), comment.getItem().getId())) {
            throw new ValidationException(
                    "Комментировать могут только бронировавшие вещь пользователи");
        }
        commentRepository.save(comment);
    }

    @Override
    public Comment getCommentById(long commentId) {
        log.info("Получение комментария с id " + commentId);
        Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.get();
    }

    @Override
    public List<Item> getItemsByRequestId(long requestId) {
        log.info("Получение предметов по запросу с id " + requestId);
        PageRequest page = PageRequest.of(0, ITEM_LIST_PAGE_SIZE);
        return itemRepository.findAllByRequestIdOrderById(requestId, page);
    }

    public boolean isUserWasAnItemBooker(long userId, long itemId) {
        PageRequest page = PageRequest.of(0, ITEM_LIST_PAGE_SIZE);
        List<Booking> bookings = bookingRepository
                .findAllByBookerIdOrderByBookingStartDesc(userId, page);
        if (bookings.size() == 0) {
            return false;
        }
        for (Booking booking : bookings) {
            if (booking.getItem().getId() == itemId && booking.getStatus()
                    .equals(Status.APPROVED) && booking.getBookingEnd().isBefore(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }

}
