package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemRequestDtoForResponse addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        final User requestor = userService.getUser(userId);
        final ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        return ItemRequestMapper.toItemRequestDtoForResponse(itemRequestRepository.save(itemRequest));
    }

    @Override
    public Collection<ItemRequestDtoForResponse> getAllRequestsOfUser(Long userId) {
        final User requestor = userService.getUser(userId);
        final Collection<ItemRequest> itemRequests
                = itemRequestRepository.findAllByRequestorOrderByCreatedDesc(requestor);
        return itemRequests.stream()
                .map(ItemRequestMapper::toItemRequestDtoForResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemRequestDtoForResponse> getAllRequestsOfOther(Long userId, int from, int size) {
        final User requestor = userService.getUser(userId);
        final Pageable pageable = PageRequest.of(from / size, size);
        final Page<ItemRequest> itemRequests =
                itemRequestRepository.findAllWhereNotEqualRequestorId(requestor.getId(), pageable);
        return itemRequests.getContent().stream()
                .map(ItemRequestMapper::toItemRequestDtoForResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoForResponse getItemRequest(Long userId, Long itemRequestId) {
        userService.validateUserExists(userId);
        final ItemRequest itemRequest = getItemRequest(itemRequestId);
        return ItemRequestMapper.toItemRequestDtoForResponse(itemRequest);
    }

    private ItemRequest getItemRequest(long id) {
        return itemRequestRepository.findById(id).orElseThrow(() -> {
            log.error("Запрос с id = {} не найден!", id);
            return new ItemRequestNotFoundException(id);
        });
    }
}