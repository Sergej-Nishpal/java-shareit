package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    private ItemRequestService itemRequestService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRequestRepository itemRequestRepository;
    User otherRequestor;
    User requestor;
    ItemRequestDto itemRequestDto;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDtoNext;
    ItemRequest itemRequestNext;
    ItemRequestDto itemRequestDtoOther;
    ItemRequest itemRequestOther;

    @BeforeEach
    void setUp() {
        itemRequestService = new ItemRequestServiceImpl(userService, itemRequestRepository);

        requestor = User.builder()
                .id(1L)
                .name("John Doe").build();

        otherRequestor = User.builder()
                .id(2L)
                .name("Sergej Nishpal").build();

        itemRequestDto = ItemRequestDto.builder()
                .description("Test itemRequest description").build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test itemRequest description")
                .requestor(requestor).build();

        itemRequestDtoNext = ItemRequestDto.builder()
                .description("Test itemRequestNext description").build();

        itemRequestNext = ItemRequest.builder()
                .id(2L)
                .description("Test itemRequestNext description")
                .requestor(requestor).build();

        itemRequestDtoOther = ItemRequestDto.builder()
                .description("Test itemRequestOther description").build();

        itemRequestOther = ItemRequest.builder()
                .id(3L)
                .description("Test itemRequestOther description")
                .requestor(otherRequestor).build();
    }

    @Test
    void addItemRequest() {
        when(userService.getUser(anyLong()))
                .thenReturn(requestor);
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        final ItemRequestDtoForResponse itemRequestDtoForResponse =
                itemRequestService.addItemRequest(requestor.getId(), itemRequestDto);

        assertNotNull(itemRequestDtoForResponse);
        assertEquals(itemRequestDto.getDescription(), itemRequestDtoForResponse.getDescription());
        verify(itemRequestRepository).save(any(ItemRequest.class));
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void getAllRequestsOfUser() {
        when(userService.getUser(anyLong()))
                .thenReturn(requestor);
        when(itemRequestRepository.findAllByRequestorOrderByCreatedDesc(any(User.class)))
                .thenReturn(List.of(itemRequest, itemRequestNext));
        Collection<ItemRequestDtoForResponse> userItemRequests =
                itemRequestService.getAllRequestsOfUser(requestor.getId());

        assertNotNull(userItemRequests);
        assertEquals(2, userItemRequests.size());
        verify(itemRequestRepository).findAllByRequestorOrderByCreatedDesc(any(User.class));
        verify(itemRequestRepository, times(1))
                .findAllByRequestorOrderByCreatedDesc(any(User.class));
    }

    @Test
    void getAllRequestsOfOther() {
        Page<ItemRequest> itemRequests = new PageImpl<>(List.of(itemRequestOther));
        when(userService.getUser(anyLong()))
                .thenReturn(requestor);
        when(itemRequestRepository.findByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(itemRequests);

        Collection<ItemRequestDtoForResponse> otherItemRequests =
                itemRequestService.getAllRequestsOfOther(requestor.getId(), 0, 10);

        assertNotNull(otherItemRequests);
        assertEquals(1, otherItemRequests.size());
        verify(itemRequestRepository).findByRequestorIdNot(anyLong(), any(Pageable.class));
        verify(itemRequestRepository, times(1))
                .findByRequestorIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getItemRequest() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        /*when(userRepository.existsById(anyLong()))
                .thenReturn(true);*/
        final ItemRequestDtoForResponse savedItemRequest =
                itemRequestService.getItemRequest(requestor.getId(), itemRequest.getId());
        assertNotNull(savedItemRequest);
        verify(itemRequestRepository).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    /*@Test
    void getItemRequestUserNotExists() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        final Long requestorId = 100L;
        final Long itemRequestId = itemRequest.getId();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequest(requestorId, itemRequestId));
        String expectedMessage = requestorId.toString();
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }*/

    @Test
    void getItemRequestById() {
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));
        final ItemRequest savedItemRequest = itemRequestService.getItemRequestById(itemRequest.getId());
        assertNotNull(savedItemRequest);
        assertEquals(itemRequest.getDescription(), savedItemRequest.getDescription());
    }
}