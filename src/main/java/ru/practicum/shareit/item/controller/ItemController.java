package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@Validated
@AllArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на добавление предмета");
        itemService.isUserExistsOrException(userId);
        Item item = itemMapper.convertToEntity(itemService, itemDto, 0L, userId);
        itemService.addItem(item);
        return itemMapper.convertToDtoForUser(itemService.getItem(item.getId()));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на обновление предмета c id " + itemId);
        itemService.isUserExistsOrException(userId);
        Item item = itemMapper.convertToEntity(itemService, itemDto, itemId, userId);
        itemService.updateItem(item);
        return itemMapper.convertToDtoForUser(itemService.getItem(item.getId()));
    }

    @DeleteMapping("/{itemId}")
    public void deleteUserById(@PathVariable Long itemId) {
        log.debug("Получен запрос на удаление предмета по id");
        itemService.deleteItemById(itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на получение предмета c id " + itemId);
        Item item = itemService.getItem(itemId);
        if (itemService.isUserAnItemOwner(userId, item)) {
            return itemMapper.convertToDtoForOwner(item);
        } else {
            return itemMapper.convertToDtoForUser(item);
        }
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsByUserSearch(@RequestParam String text,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.debug("Получен запрос на получение всех предметов пользователя с id" + userId
                + " и фильтром " + text);
        ArrayList<Item> items = new ArrayList<>(
                itemService.getItemsByNameOrDescriptionSearch(text, from, size));
        return itemMapper
                .convertToDtoListOfItems(items, itemService.isUserAnItemsOwner(userId, items));
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.debug("Получен запрос на получение всех предметов пользователя с id" + userId);
        ArrayList<Item> items = new ArrayList<>(itemService.getUserItems(userId, from, size));
        return itemMapper
                .convertToDtoListOfItems(items, itemService.isUserAnItemsOwner(userId, items));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@Valid @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.debug("Получен запрос на добавление комментария от пользователя " + userId);
        Comment comment = commentMapper.convertToEntity(commentDto, userId, itemId);
        itemService.addCommentToItem(comment);
        return commentMapper.convertToDto(itemService.getCommentById(comment.getId()));
    }
}