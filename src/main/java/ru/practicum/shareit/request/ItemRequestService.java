package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequestDtoForResponse addItemRequest(Long userId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDtoForResponse> getAllRequestsOfUser(Long userId);

    Collection<ItemRequestDtoForResponse> getAllRequestsOfOther(Long userId, int from, int size);

    ItemRequestDtoForResponse getItemRequest(Long userId, Long requestId);
}