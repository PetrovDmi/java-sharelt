package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.controller.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class RequestMapper {

    private final ModelMapper modelMapper = new ModelMapper();
    private final ItemMapper itemMapper;

    public ItemRequestDto convertToDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = modelMapper.map(itemRequest, ItemRequestDto.class);
        itemRequestDto.setRequesterId(itemRequest.getId());
        itemRequestDto.setItems(itemMapper.convertToDtoListOfItems(itemRequest.getItems(),
                false));
        return itemRequestDto;
    }

    public List<ItemRequestDto> convertToDtoListOfItemRequests(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(convertToDto(itemRequest));
        }
        return itemRequestDtos;
    }

    public ItemRequest convertToEntity(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = modelMapper.map(itemRequestDto, ItemRequest.class);

        if (itemRequest.getCreated() == null) {
            itemRequest.setCreated(LocalDateTime.now());
        }
        return itemRequest;
    }

}
