package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForResponse;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;
    UserDto requestor;
    ItemDtoForResponse itemDtoForResponse;
    ItemRequestDto itemRequestDto;
    ItemRequestDtoForResponse itemRequestDtoForResponse;

    @BeforeEach
    void setUp() {
        requestor = UserDto.builder()
                .id(1L)
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();

        itemDtoForResponse = ItemDtoForResponse.builder()
                .id(1L)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .description("ItemRequestDto description").build();

        itemRequestDtoForResponse = ItemRequestDtoForResponse.builder()
                .id(1L)
                .description("ItemRequestDto description")
                .created(LocalDateTime.now().minusDays(1))
                .requestor(requestor)
                .items(List.of(itemDtoForResponse)).build();
    }

    @Test
    void addItemRequest() throws Exception {
        when(itemRequestService.addItemRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDtoForResponse);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoForResponse.getDescription())));

        verify(itemRequestService, times(1))
                .addItemRequest(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void getItemRequestsOfUser() throws Exception {
        when(itemRequestService.getAllRequestsOfUser(anyLong()))
                .thenReturn(List.of(itemRequestDtoForResponse));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoForResponse.getDescription())));

        verify(itemRequestService, times(1))
                .getAllRequestsOfUser(anyLong());
    }

    @Test
    void getItemRequestsOfOther() throws Exception {
        when(itemRequestService.getAllRequestsOfOther(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoForResponse));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", requestor.getId())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDtoForResponse.getDescription())));

        verify(itemRequestService, times(1))
                .getAllRequestsOfOther(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getItemRequest() throws Exception {
        when(itemRequestService.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoForResponse);

        mockMvc.perform(get("/requests/{requestId}", requestor.getId())
                        .header("X-Sharer-User-Id", requestor.getId())
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoForResponse.getDescription())));

        verify(itemRequestService, times(1))
                .getItemRequest(anyLong(), anyLong());
    }
}