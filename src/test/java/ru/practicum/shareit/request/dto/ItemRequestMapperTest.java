package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestMapperTest {

    @Autowired
    private JacksonTester<ItemRequest> jsonItemRequest;

    @Autowired
    private JacksonTester<ItemRequestDtoForResponse> jsonItemRequestDtoForResponse;

    @Test
    void toItemRequest() throws IOException {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(User.builder()
                        .id(1L)
                        .name("Sergej Nishpal")
                        .email("sergej.nishpal@yandex.ru")
                        .build())
                .description("Test itemRequest description")
                .created(LocalDateTime.of(2022,10,21, 19, 30, 0))
                .build();

        JsonContent<ItemRequest> result = jsonItemRequest.write(itemRequest);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.requestor.name").isEqualTo("Sergej Nishpal");
        assertThat(result)
                .extractingJsonPathStringValue("$.requestor.email")
                .isEqualTo("sergej.nishpal@yandex.ru");
        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Test itemRequest description");
        assertThat(result)
                .extractingJsonPathStringValue("$.created").isEqualTo("2022-10-21T19:30:00");
    }

    @Test
    void toItemRequestDtoForResponse() throws IOException {
        ItemRequestDtoForResponse itemRequestDtoForResponse = ItemRequestDtoForResponse.builder()
                .id(1L)
                .requestor(UserDto.builder()
                        .id(1L)
                        .name("Sergej Nishpal")
                        .email("sergej.nishpal@yandex.ru")
                        .build())
                .description("Test itemRequest description")
                .created(LocalDateTime.of(1975,2,14, 19, 35, 0))
                .build();

        JsonContent<ItemRequestDtoForResponse> result = jsonItemRequestDtoForResponse.write(itemRequestDtoForResponse);

        assertThat(result)
                .extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathNumberValue("$.requestor.id").isEqualTo(1);
        assertThat(result)
                .extractingJsonPathStringValue("$.requestor.name").isEqualTo("Sergej Nishpal");
        assertThat(result)
                .extractingJsonPathStringValue("$.requestor.email")
                .isEqualTo("sergej.nishpal@yandex.ru");
        assertThat(result)
                .extractingJsonPathStringValue("$.description")
                .isEqualTo("Test itemRequest description");
        assertThat(result)
                .extractingJsonPathStringValue("$.created").isEqualTo("1975-02-14T19:35:00");
    }
}