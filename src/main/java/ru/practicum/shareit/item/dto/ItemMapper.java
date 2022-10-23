package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemDtoForResponse toItemDtoForResponse(Item item, Booking lastBooking, Booking nextBooking) {
        final Collection<CommentDtoForResponse> comments = item.getComments().stream()
                .map(CommentMapper::toCommentDtoForResponse)
                .collect(Collectors.toList());

        return ItemDtoForResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking == null ? null : getBookingData(lastBooking))
                .nextBooking(nextBooking == null ? null : getBookingData(nextBooking))
                .comments(comments)
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
                .build();
    }

    private static ItemDtoForResponse.BookingDtoForItem getBookingData(Booking booking) {
        return ItemDtoForResponse.BookingDtoForItem.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}