package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .created(LocalDateTime.now())
                .requestor(requestor)
                .build();
    }

    public static ItemRequestDtoForResponse toItemRequestDtoForResponse(ItemRequest itemRequest) {
        Collection<ItemDtoForResponse> items = null;
        if (itemRequest.getItems() != null) {
            items = itemRequest.getItems().stream()
                    .map(item -> ItemMapper.toItemDtoForResponse(item, null, null))
                    .collect(Collectors.toList());
        }

        return ItemRequestDtoForResponse.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(UserMapper.toUserDto(itemRequest.getRequestor()))
                .items(items)
                .build();
    }
}