package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoForResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForResponse;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private ItemDto itemDtoToAdd;
    private ItemDto itemDtoAdded;
    private ItemDto itemDtoToUpdate;
    private ItemDto itemDtoUpdated;
    private ItemDtoForResponse itemDtoForResponse;
    private CommentDto commentDto;
    private CommentDtoForResponse commentDtoForResponse;

    @BeforeEach
    void setUp() {
        itemDtoToAdd = ItemDto.builder()
                .id(null)
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .build();

        itemDtoAdded = ItemDto.builder()
                .id(1L)
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .build();

        itemDtoToUpdate = ItemDto.builder()
                .name("Отвёртка")
                .description("Крестовая")
                .available(true)
                .build();

        itemDtoUpdated = ItemDto.builder()
                .id(1L)
                .name("Отвёртка")
                .description("Шлицевая")
                .available(true)
                .build();

        itemDtoForResponse = ItemDtoForResponse.builder()
                .id(1L)
                .name("Отвёртка")
                .description("Шлицевая")
                .available(true)
                .build();

        commentDto = CommentDto.builder()
                .text("Cool!")
                .build();

        commentDtoForResponse = CommentDtoForResponse.builder()
                .id(1L)
                .itemName("Отвёртка")
                .authorName("Sergej")
                .text("Cool!")
                .created(LocalDateTime.of(2022, 2, 14, 12, 0))
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void add() throws Exception {
        when(itemService.add(anyLong(), any()))
                .thenReturn(itemDtoAdded);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoToAdd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoAdded.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoAdded.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoAdded.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoAdded.getAvailable())));

        verify(itemService, times(1))
                .add(anyLong(), any());
    }

    @Test
    void update() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(itemDtoUpdated);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoToUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoUpdated.getAvailable())));

        verify(itemService, times(1))
                .update(anyLong(), anyLong(), any());
    }

    @Test
    void getById() throws Exception {
        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemDtoForResponse);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoForResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoForResponse.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoForResponse.getAvailable())));

        verify(itemService, times(1))
                .getById(anyLong(), anyLong());
    }

    @Test
    void getAll() throws Exception {
        when(itemService.getAll(anyLong()))
                .thenReturn(List.of(itemDtoForResponse));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoForResponse.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoForResponse.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoForResponse.getAvailable())));

        verify(itemService, times(1))
                .getAll(anyLong());
    }

    @Test
    void getByText() throws Exception {
        when(itemService.getByText(anyLong(), anyString()))
                .thenReturn(List.of(itemDtoUpdated));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "anyString()")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemDtoUpdated.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDtoUpdated.getName())))
                .andExpect(jsonPath("$[0].description", is(itemDtoUpdated.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemDtoUpdated.getAvailable())));

        verify(itemService, times(1))
                .getByText(1L, "anyString()");

    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(any(), anyLong(), anyLong()))
                .thenReturn(commentDtoForResponse);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoForResponse.getId()), Long.class))
                .andExpect(jsonPath("$.itemName", is(commentDtoForResponse.getItemName())))
                .andExpect(jsonPath("$.authorName", is(commentDtoForResponse.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDtoForResponse.getText())))
                .andExpect(jsonPath("$.created", is(commentDtoForResponse.getCreated()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")))));

        verify(itemService, times(1))
                .addComment(any(), anyLong(), anyLong());
    }
}