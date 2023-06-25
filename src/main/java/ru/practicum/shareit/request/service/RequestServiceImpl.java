package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest, long userId) {
        log.info("Создание нового запроса на предмет");
        User requester = userService.getUserById(userId);
        itemRequest.setRequester(requester);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequest getItemRequestById(long requestId, long userId) {
        log.info("Получение запроса на предмет с id " + requestId);
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(requestId);
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь не был найден");
        }
        if (optionalItemRequest.isEmpty()) {
            throw new NotFoundException("Запрос на предмет с id " + requestId + " не обнаружен!");
        }
        optionalItemRequest.get()
                .setItems(itemService.getItemsByRequestId(optionalItemRequest.get().getId()));
        return optionalItemRequest.get();
    }

    @Override
    public List<ItemRequest> getUserRequestsById(long userId) {
        log.info("Получение запросов на предметы от пользователя с id " + userId);
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь не был найден");
        }

        return setItemsForListOfRequests(
                itemRequestRepository.findAllByRequesterIdOrderByIdDesc(userId));
    }

    @Override
    public List<ItemRequest> getOtherUsersRequests(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        if (!userService.isUserExists(userId)) {
            throw new NotFoundException("Пользователь не был найден");
        }
        return setItemsForListOfRequests(
                itemRequestRepository.findByRequesterNotOrderByIdDesc(userId, page));
    }

    private List<ItemRequest> setItemsForListOfRequests(List<ItemRequest> itemRequests) {
        if (itemRequests == null || itemRequests.size() == 0) {
            return new ArrayList<>();
        }
        for (ItemRequest itemRequest : itemRequests) {
            itemRequest.setItems(itemService.getItemsByRequestId(itemRequest.getId()));
        }
        return itemRequests;
    }
}
