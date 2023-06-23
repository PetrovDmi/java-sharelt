package ru.practicum.shareit.item.service;

import java.util.List;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

public interface ItemService {

    void addItem(Item item);

    void updateItem(Item item);

    Item getItem(long itemId);

    void deleteItemById(long itemId);

    List<Item> getItemsByNameOrDescriptionSearch(String text, int from, int size);

    List<Item> getUserItems(long userId, int from, int size);

    boolean isItemExists(long itemId);

    boolean isItemAvailable(long itemId);

    void isUserExistsOrException(long userId);

    boolean isUserAnItemOwner(long userId, Item item);

    boolean isUserAnItemsOwner(long userId, List<Item> items);

    void addCommentToItem(Comment comment);

    Comment getCommentById(long commentId);

    List<Item> getItemsByRequestId(long requestId);
}
