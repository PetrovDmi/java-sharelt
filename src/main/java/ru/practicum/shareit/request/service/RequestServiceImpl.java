package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private static final int ITEM_LIST_PAGE_SIZE = 10;

    @Override
    public ItemRequest addItemRequest(ItemRequest itemRequest, long userId) {
        log.info("Создание нового запроса на предмет");
        Optional<User> requester = userRepository.findById(userId);
        if (requester.isEmpty()) {
            throw new NotFoundException("Пользователь не был найден");
        }
        itemRequest.setRequester(requester.get());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequest getItemRequestById(long requestId, long userId) {
        log.info("Получение запроса на предмет с id " + requestId);
        Optional<ItemRequest> optionalItemRequest = itemRequestRepository.findById(requestId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не был найден");
        }
        if (optionalItemRequest.isEmpty()) {
            throw new NotFoundException("Запрос на предмет с id " + requestId + " не обнаружен!");
        }
        PageRequest page = PageRequest.of(0, ITEM_LIST_PAGE_SIZE);
        itemRepository.findAllByRequestIdOrderById(requestId, page);
        return optionalItemRequest.get();
    }

    @Override
    public List<ItemRequest> getUserRequestsById(long userId) {
        log.info("Получение запросов на предметы от пользователя с id " + userId);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не был найден");
        }
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        return setItemsForListOfRequests(itemRequestRepository.findAllByRequesterIdOrderByIdDesc(userId, pageable).getContent());
    }

    @Override
    public List<ItemRequest> getOtherUsersRequests(long userId, int from, int size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException("Пользователь не был найден");
        }
        Page<ItemRequest> pageResult = itemRequestRepository.findByRequesterNotOrderByIdDesc(userId, page);
        return setItemsForListOfRequests(pageResult.getContent());
    }

    private List<ItemRequest> setItemsForListOfRequests(List<ItemRequest> itemRequests) {
        if (itemRequests == null || itemRequests.size() == 0) {
            return new ArrayList<>();
        }
        for (ItemRequest itemRequest : itemRequests) {
            PageRequest page = PageRequest.of(0, ITEM_LIST_PAGE_SIZE);
            itemRepository.findAllByRequestIdOrderById(itemRequest.getId(), page);
        }
        return itemRequests;
    }
}
