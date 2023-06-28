package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
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
    private ItemRepository mockItemRepository;
    private UserRepository mockUserRepository;
    private final PageRequest page = PageRequest.of(0, 10);

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
        mockItemRepository = Mockito.mock(ItemRepository.class);
        mockUserRepository = Mockito.mock(UserRepository.class);
        requestService = new RequestServiceImpl(mockItemRequestRepository, mockUserRepository,
                mockItemRepository);

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

        Pageable pageable = PageRequest.of(0, 10);
        Mockito.when(mockItemRequestRepository.findAllByRequesterIdOrderByIdDesc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(itemRequestTestMap.get(1L), itemRequestTestMap.get(3L))));
        Mockito.when(mockItemRequestRepository.findByRequesterNotOrderByIdDesc(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(itemRequestTestMap.get(2L))));
        Mockito.when(mockItemRequestRepository.save(itemRequestTestMap.get(1L)))
                .thenReturn(itemRequestTestMap.get(1L));
        Mockito.when(mockItemRequestRepository.save(itemRequestTestMap.get(2L)))
                .thenReturn(itemRequestTestMap.get(2L));
        Mockito.when(mockItemRequestRepository.save(itemRequestTestMap.get(3L)))
                .thenReturn(itemRequestTestMap.get(3L));
    }

    /*@Test
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
    }*/
    @Test
    void addItemRequestShouldCallRepositorySaveMethod() {
        // Создание объекта запроса предмета из начальных данных
        ItemRequest itemRequest = itemRequestTestMap.get(1L);

        // Создание пользователя из начальных данных
        User requester = userTestMap.get(itemRequest.getRequester().getId());

        // Мокирование метода findById у userRepository
        Mockito.when(mockUserRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        // Вызов метода addItemRequest
        ItemRequest result = requestService.addItemRequest(itemRequest, requester.getId());

        // Проверка вызова метода save у mockItemRequestRepository
        Mockito.verify(mockItemRequestRepository, Mockito.times(1)).save(itemRequest);

        // Проверка ожидаемых значений
        Assertions.assertEquals(itemRequest.getId(), result.getId());
        Assertions.assertEquals(itemRequest.getDescription(), result.getDescription());
        Assertions.assertEquals(itemRequest.getCreated(), result.getCreated());
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
        Assertions.assertEquals("Пользователь не был найден",
                exception.getMessage());
    }

    @Test
    void getItemRequestByIdShouldReturnRequest() {
        // Создание объекта запроса предмета из начальных данных
        ItemRequest itemRequest = itemRequestTestMap.get(1L);

        // Создание пользователя из начальных данных
        User requester = userTestMap.get(itemRequest.getRequester().getId());

        // Мокирование метода findById у userRepository
        Mockito.when(mockUserRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        // Вызов метода getItemRequestById
        ItemRequest result = requestService.getItemRequestById(itemRequest.getId(), requester.getId());

        // Проверка ожидаемых значений
        Assertions.assertEquals(itemRequest.getId(), result.getId());
        Assertions.assertEquals(itemRequest.getCreated(), result.getCreated());
        Assertions.assertEquals(itemRequest.getDescription(), result.getDescription());
        Assertions.assertEquals(itemRequest.getRequester().getId(), result.getRequester().getId());
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
        User requester = userTestMap.get(1L);
        Mockito.when(mockUserRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        List<ItemRequest> itemRequests = new ArrayList<>();
        Page<ItemRequest> page = new PageImpl<>(itemRequests);
        Mockito.when(mockItemRequestRepository.findAllByRequesterIdOrderByIdDesc(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(page);

        List<ItemRequest> result = requestService.getUserRequestsById(requester.getId());

        Assertions.assertEquals(itemRequests.size(), result.size());
    }

    @Test
    void getOtherUsersRequestsShouldReturnListOfRequests() {
        User currentUser = userTestMap.get(1L);
        Mockito.when(mockUserRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(currentUser));

        List<ItemRequest> itemRequests = requestService.getOtherUsersRequests(1, 0, 10);
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
