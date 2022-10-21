package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;
    User user1;
    User user2;
    Item item1;
    Item item2;
    private BookingDto bookingDto;
    private BookingDtoForResponse bookingDtoForResponse;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();

        item1 = Item.builder()
                .id(1L)
                .owner(user1)
                .name("Test item 1 SpEcIaL name")
                .description("Test item 1 description")
                .available(true).build();

        item2 = Item.builder()
                .id(2L)
                .owner(user2)
                .name("Test item 2 name")
                .description("Test item 2 description")
                .available(false).build();

        bookingDto = BookingDto.builder()
                .itemId(item1.getId())
                .bookerId(user2.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.WAITING).build();

        bookingDtoForResponse = BookingDtoForResponse.builder()
                .booker(user2)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.APPROVED).build();
    }

    @Test
    void addBooking() throws Exception {
        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(item1.getId()), Long.class))
                .andExpect(jsonPath("$.bookerId", is(user2.getId()), Long.class));

        verify(bookingService, times(1))
                .addBooking(anyLong(), any(BookingDto.class));
    }

    @Test
    void addBookingIncorrectBecauseStartAfterEnd() throws Exception {
        bookingDto.setStart(bookingDto.getEnd().plusDays(1));

        when(bookingService.addBooking(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2L)
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoForResponse);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booker", is(user2), User.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name()), BookingStatus.class));

        verify(bookingService, times(1))
                .approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void getBookingInfo() throws Exception {
        when(bookingService.getBookingInfo(anyLong(), anyLong()))
                .thenReturn(bookingDtoForResponse);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                .header("X-Sharer-User-Id", 1L)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.booker", is(user2), User.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.name()), BookingStatus.class));

        verify(bookingService, times(1))
                .getBookingInfo(anyLong(), anyLong());
    }

    @Test
    void getBookings() throws Exception {
        when(bookingService.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoForResponse));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].booker", is(user2), User.class))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name()), BookingStatus.class));

        verify(bookingService, times(1))
                .getBookings(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void getBookingsOfOwner() throws Exception {
        when(bookingService.getBookingsOfOwner(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoForResponse));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].booker", is(user2), User.class))
                .andExpect(jsonPath("$[0].status", is(BookingStatus.APPROVED.name()), BookingStatus.class));

        verify(bookingService, times(1))
                .getBookingsOfOwner(anyLong(), anyString(), anyInt(), anyInt());
    }
}