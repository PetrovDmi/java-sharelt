package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    ItemRequest addItemRequest(ItemRequest itemRequest, long userId);

    ItemRequest getItemRequestById(long requestId, long userId);

    List<ItemRequest> getUserRequestsById(long userId);

    List<ItemRequest> getOtherUsersRequests(long userId, int from, int size);
}
