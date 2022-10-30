package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemMapperTest {

    @Autowired
    private JacksonTester<Item> jsonItem;

    @Autowired
    private JacksonTester<ItemDto> jsonItemDto;

    @Autowired
    private JacksonTester<ItemDtoForResponse> jsonItemDtoForResponse;

    @Test
    void toItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item name")
                .description("Test item description")
                .requestId(1L)
                .available(true)
                .build();

        JsonContent<ItemDto> result = jsonItemDto.write(itemDto);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.name").isEqualTo("Test item name");
        assertThat(result)
                .extractingJsonPathStringValue("$.description").isEqualTo("Test item description");
        assertThat(result)
                .extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @Test
    void toItem() throws IOException {
        User owner = User.builder()
                .id(1L)
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru").build();
        Item item = Item.builder()
                .id(1L)
                .name("Test item name")
                .description("Test item description")
                .available(true)
                .owner(owner).build();

        JsonContent<Item> result = jsonItem.write(item);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.name").isEqualTo("Test item name");
        assertThat(result)
                .extractingJsonPathStringValue("$.description").isEqualTo("Test item description");
        assertThat(result)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result)
                .extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.owner.name").isEqualTo("Sergej Nishpal");
        assertThat(result)
                .extractingJsonPathStringValue("$.owner.email").isEqualTo("sergej.nishpal@yandex.ru");
    }

    @Test
    void toItemDtoForResponse() throws IOException {
        ItemDtoForResponse itemDtoForResponse = ItemDtoForResponse.builder()
                .id(1L)
                .name("Test item name")
                .description("Test item description")
                .requestId(1L)
                .lastBooking(ItemDtoForResponse.BookingDtoForItem.builder()
                        .id(1L)
                        .bookerId(2L)
                        .start(LocalDateTime.of(2022, 10, 1, 12, 0, 0))
                        .end(LocalDateTime.of(2022, 10, 2, 12, 0, 0))
                        .build())
                .nextBooking(ItemDtoForResponse.BookingDtoForItem.builder()
                        .id(1L)
                        .bookerId(1L)
                        .start(LocalDateTime.of(2022, 12, 1, 12, 0, 0))
                        .end(LocalDateTime.of(2022, 12, 2, 12, 0, 0))
                        .build())
                .available(false)
                .comments(List.of(CommentDtoForResponse.builder()
                        .id(1L)
                        .itemName("Test item name")
                        .authorName("Sergej Nishpal")
                        .created(LocalDateTime.of(2022, 10, 21, 19, 30, 0))
                        .text("Test text")
                        .build()))
                .build();

        JsonContent<ItemDtoForResponse> result = jsonItemDtoForResponse.write(itemDtoForResponse);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.name").isEqualTo("Test item name");
        assertThat(result)
                .extractingJsonPathStringValue("$.description").isEqualTo("Test item description");
        assertThat(result)
                .extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(2);
        assertThat(result)
                .extractingJsonPathStringValue("$.lastBooking.start").isEqualTo("2022-10-01T12:00:00");
        assertThat(result)
                .extractingJsonPathStringValue("$.lastBooking.end").isEqualTo("2022-10-02T12:00:00");
        assertThat(result)
                .extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathNumberValue("$.nextBooking.bookerId").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.nextBooking.start").isEqualTo("2022-12-01T12:00:00");
        assertThat(result)
                .extractingJsonPathStringValue("$.nextBooking.end").isEqualTo("2022-12-02T12:00:00");
        assertThat(result)
                .extractingJsonPathBooleanValue("$.available").isEqualTo(false);
        assertThat(result)
                .extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.comments[0].itemName").isEqualTo("Test item name");
        assertThat(result)
                .extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("Sergej Nishpal");
        assertThat(result)
                .extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo("2022-10-21T19:30:00");
        assertThat(result)
                .extractingJsonPathStringValue("$.comments[0].text").isEqualTo("Test text");
    }
}