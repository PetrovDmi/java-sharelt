package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private RequestService requestService;
    @Autowired
    private UserService userService;
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
        userService.addUser(userTestMap.get(1L));
        userService.addUser(userTestMap.get(2L));
        userService.addUser(userTestMap.get(3L));
    }

    @Test
    void addItemRequestShouldWriteToDbAndReturn() {
        ItemRequest itemRequest = requestService.addItemRequest(itemRequestTestMap.get(1L),
                itemRequestTestMap.get(1L).getRequester().getId());

        Assertions.assertEquals(itemRequestTestMap.get(1L).getId(), itemRequest.getId());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getCreated(), itemRequest.getCreated());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getRequester().getId(),
                itemRequest.getRequester().getId());
        Assertions.assertEquals(itemRequestTestMap.get(1L).getDescription(),
                itemRequest.getDescription());
    }

    @Test
    void getItemRequestByIdShouldReturnItemRequest() {
        requestService.addItemRequest(itemRequestTestMap.get(3L),
                itemRequestTestMap.get(3L).getRequester().getId());

        ItemRequest itemRequest = requestService.getItemRequestById(1L, 1L);

        Assertions.assertEquals(1, itemRequest.getId());
        Assertions.assertEquals(itemRequestTestMap.get(3L).getCreated(), itemRequest.getCreated());
        Assertions.assertEquals(itemRequestTestMap.get(3L).getRequester().getId(),
                itemRequest.getRequester().getId());
        Assertions.assertEquals(itemRequestTestMap.get(3L).getDescription(),
                itemRequest.getDescription());
    }

    @Test
    void getUserRequestsByIdShouldReturnListOfItemRequests() {
        requestService.addItemRequest(itemRequestTestMap.get(1L),
                itemRequestTestMap.get(1L).getRequester().getId());
        requestService.addItemRequest(itemRequestTestMap.get(2L),
                itemRequestTestMap.get(2L).getRequester().getId());
        requestService.addItemRequest(itemRequestTestMap.get(3L),
                itemRequestTestMap.get(3L).getRequester().getId());

        List<ItemRequest> itemRequests = requestService.getUserRequestsById(1L);

        Assertions.assertEquals(2, itemRequests.size());
    }

    @Test
    void getOtherUsersRequestsShouldReturnListOfItemRequests() {
        requestService.addItemRequest(itemRequestTestMap.get(1L),
                itemRequestTestMap.get(1L).getRequester().getId());
        requestService.addItemRequest(itemRequestTestMap.get(2L),
                itemRequestTestMap.get(2L).getRequester().getId());
        requestService.addItemRequest(itemRequestTestMap.get(3L),
                itemRequestTestMap.get(3L).getRequester().getId());

        List<ItemRequest> itemRequests = requestService.getOtherUsersRequests(1L,
                0, 10);

        Assertions.assertEquals(1, itemRequests.size());
    }
}
