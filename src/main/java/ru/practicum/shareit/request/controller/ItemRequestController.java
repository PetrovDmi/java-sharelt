package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
@Validated
public class ItemRequestController {

    private final RequestMapper requestMapper;
    private final RequestService requestService;


    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на добавление нового запроса на предмет");
        ItemRequest itemRequest = requestMapper.convertToEntity(itemRequestDto);
        return requestMapper.convertToDto(requestService.addItemRequest(itemRequest, userId));
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@PathVariable final Long id,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на получения запроса на предмет с id " + id);
        return requestMapper.convertToDto(requestService.getItemRequestById(id, userId));
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.debug("Получен запрос на получения запросов на предмет от пользователя с id " + userId);
        return requestMapper
                .convertToDtoListOfItemRequests(requestService.getUserRequestsById(userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.debug("Получен запрос на получения запросов на предмет других пользователей");
        return requestMapper
                .convertToDtoListOfItemRequests(
                        requestService.getOtherUsersRequests(userId, from, size));
    }

}
