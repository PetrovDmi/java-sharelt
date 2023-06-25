package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.controller.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class ItemMapper {

    private final ModelMapper modelMapper = new ModelMapper();
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public ItemDto convertToDtoForOwner(Item item) {
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        Booking lastBooking = bookingService.getItemLastBooking(item.getId());
        Booking nextBooking = bookingService.getItemNextBooking(item.getId());
        itemDto.setLastBooking(bookingMapper.convertToShort(lastBooking));
        itemDto.setNextBooking(bookingMapper.convertToShort(nextBooking));
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        itemDto.setComments(commentMapper.convertToDtoListOfComments(comments));
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public ItemDto convertToDtoForUser(Item item) {
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        itemDto.setLastBooking(null);
        itemDto.setNextBooking(null);
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        itemDto.setComments(commentMapper.convertToDtoListOfComments(comments));
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public List<ItemDto> convertToDtoListOfItems(List<Item> items, boolean isOwner) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : items) {
            if (isOwner) {
                itemDtos.add(convertToDtoForOwner(item));
            } else {
                itemDtos.add(convertToDtoForUser(item));
            }
        }
        return itemDtos;
    }

    public Item convertToEntity(ItemService itemService, ItemDto itemDto, Long itemId,
                                Long userId) {
        Item item = modelMapper.map(itemDto, Item.class);
        if (itemId != 0) {
            item.setId(itemId);
        }
        item.setUserId(userId);
        Item oldItem;

        if (itemService.isItemExists(itemId)) {
            oldItem = itemService.getItem(itemId);
            if (oldItem.getUserId() != userId) {
                throw new NotFoundException("Пользователь не найден!");
            }
            if (item.getName() == null) {
                item.setName(oldItem.getName());
            }
            if (item.getDescription() == null) {
                item.setDescription(oldItem.getDescription());
            }
            if (item.getAvailable() == null) {
                item.setAvailable(oldItem.getAvailable());
            }
        }
        return item;
    }
}
