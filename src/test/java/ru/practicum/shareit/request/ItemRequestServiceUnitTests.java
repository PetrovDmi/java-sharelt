package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class ItemRequestServiceUnitTests {

    private RequestService requestService;
    private RequestRepository mockItemRequestRepository;
    private UserService mockUserService;
    private ItemService itemService;
    private PageRequest page = PageRequest.of(0, 10);

    LocalDateTime created = LocalDateTime.of(2023, 5, 19,
            10, 0, 0);

    private final Map<Long, User> userTestMap = Map.of(
            1L, new User(1, "testUserOne", "testUserOne@yandex.ru"),
            2L, new User(2, "testUserTwo", "testUserTwo@yandex.ru"),
            3L, new User(3, "testUserThree", "testUserThree@yandex.ru")
    );

    private final Map<Long, ItemRequest> itemRequestTestMap = Map.of(
            1L, new ItemRequest(1, "Хочу дрель", userTestMap.get(1L),
                    created, null),
            2L, new ItemRequest(2, "Хочу дрель", userTestMap.get(2L),
                    created, null),
            3L, new ItemRequest(3, "Хочу молоток", userTestMap.get(1L),
                    created, null)
    );

    @BeforeEach
    void setUp() {
        mockItemRequestRepository = Mockito.mock(RequestRepository.class);
        mockUserService = Mockito.mock(UserService.class);
        itemService = Mockito.mock(ItemService.class);
        requestService = new RequestServiceImpl(mockItemRequestRepository, mockUserService,
                itemService);

        Mockito.when(itemService.getItemsByRequestId(Mockito.anyLong()))
                .thenReturn(new ArrayList<>());

        Mockito.when(mockItemRequestRepository.findById(1L)).thenReturn(
                Optional.ofNullable(itemRequestTestMap.get(1L)));
        Mockito.when(mockItemRequestRepository.findById(2L)).thenReturn(
                Optional.ofNullable(itemRequestTestMap.get(2L)));
        Mockito.when(mockItemRequestRepository.findById(3L)).thenReturn(
                Optional.ofNullable(itemRequestTestMap.get(3L)));
        Mockito.when(mockItemRequestRepository.findById(4L)).thenReturn(
                Optional.empty());

        Mockito.when(mockUserService.isUserExists(1L)).thenReturn(true);
        Mockito.when(mockUserService.isUserExists(2L)).thenReturn(true);
        Mockito.when(mockUserService.isUserExists(3L)).thenReturn(true);
        Mockito.when(mockUserService.isUserExists(4L)).thenReturn(false);

        Mockito.when(mockItemRequestRepository.findAllByRequesterIdOrderByIdDesc(1))
                .thenReturn(List.of(itemRequestTestMap.get(1L), itemRequestTestMap.get(3L)));
        Mockito.when(mockItemRequestRepository.findByRequesterNotOrderByIdDesc(1, page))
                .thenReturn(List.of(itemRequestTestMap.get(2L)));
        Mockito.when(mockItemRequestRepository.save(itemRequestTestMap.get(1L)))
                .thenReturn(itemRequestTestMap.get(1L));
        Mockito.when(mockItemRequestRepository.save(itemRequestTestMap.get(2L)))
                .thenReturn(itemRequestTestMap.get(2L));
        Mockito.when(mockItemRequestRepository.save(itemRequestTestMap.get(3L)))
                .thenReturn(itemRequestTestMap.get(3L));
    }

    @Test
    void addItemRequestShouldCallRepositorySaveMethod() {
        ItemRequest itemRequest = requestService.addItemRequest(itemRequestTestMap.get(1L),
                itemRequestTestMap.get(1L).getRequester().getId());
        Mockito.verify(mockItemRequestRepository, Mockito.times(1))
                .save(itemRequestTestMap.get(1L));
        Assertions.assertEquals(itemRequestTestMap.get(1L).getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getDescription(),
                itemRequest.getDescription());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getCreated(),
                itemRequest.getCreated());
    }

    @Test
    void getItemRequestByIdShouldThrowNotFoundExceptionUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.getItemRequestById(1, 4)
        );
        Assertions.assertEquals("Пользователь не был найден", exception.getMessage());
    }

    @Test
    void getItemRequestByIdShouldThrowNotFoundExceptionRequest() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.getItemRequestById(4, 1)
        );
        Assertions.assertEquals("Запрос на предмет с id 4 не обнаружен!",
                exception.getMessage());
    }

    @Test
    void getItemRequestByIdShouldReturnRequest() {
        ItemRequest itemRequest = requestService.getItemRequestById(1, 1);
        Assertions.assertEquals(itemRequestTestMap.get(1L).getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getCreated(), itemRequest.getCreated());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getDescription(),
                itemRequest.getDescription());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getRequester().getId(),
                itemRequest.getRequester().getId());
    }

    @Test
    void getUserRequestsByIdShouldThrowNotFoundExceptionUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.getUserRequestsById(4)
        );
        Assertions.assertEquals("Пользователь не был найден", exception.getMessage());
    }

    @Test
    void getUserRequestsByIdShouldReturnListOfRequests() {
        List<ItemRequest> itemRequests = requestService.getUserRequestsById(1);
        Assertions.assertEquals(2, itemRequests.size());
    }

    @Test
    void getOtherUsersRequestsShouldReturnListOfRequests() {
        List<ItemRequest> itemRequests = requestService.getOtherUsersRequests(1,
                0, 10);
        Assertions.assertEquals(1, itemRequests.size());
    }

    @Test
    void getOtherUsersRequestsShouldThrowNotFoundExceptionUser() {
        final NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> requestService.getOtherUsersRequests(4,
                        0, 10)
        );
        Assertions.assertEquals("Пользователь не был найден", exception.getMessage());
    }
}
