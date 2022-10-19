package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestService itemRequestService;
    User owner;
    User booker;
    ItemRequest itemRequest;
    ItemDto itemDto;
    Item item;
    Comment comment;
    CommentDto commentDto;
    Booking currentBooking;
    Booking futureBooking;

    @BeforeEach
    void setUp() {
        itemService = new ItemServiceImpl(userService, itemRepository,
                bookingRepository, commentRepository, itemRequestService);

        owner = User.builder()
                .id(1L)
                .name("Sergej").build();

        booker = User.builder()
                .id(2L)
                .name("Ivan").build();

        itemRequest = ItemRequest.builder()
                .description("Test request description").build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item name")
                .description("Test item description")
                .available(true).build();

        item = Item.builder()
                .id(1L)
                .name("Test item name")
                .owner(owner)
                .description("Test item description").build();

        comment = Comment.builder()
                .id(1L)
                .item(item)
                .author(booker)
                .text("Test comment")
                .created(LocalDateTime.now()).build();

        commentDto = CommentDto.builder()
                .text("Test comment").build();

        currentBooking = Booking.builder()
                .id(1L)
                .item(item)
                .build();

        futureBooking = Booking.builder()
                .id(2L)
                .build();
    }

    @Test
    void add() {
        when(userService.getUser(anyLong()))
                .thenReturn(owner);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        ItemDto savedItemDto =
                ItemMapper.toItemDto(ItemMapper.toItem(itemService.add(owner.getId(), itemDto), owner, itemRequest));
        assertNotNull(savedItemDto);
        assertEquals(itemDto, savedItemDto);
        verify(itemRepository).save(any(Item.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void update() {
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        ItemDto updatedItemDto =
                ItemMapper.toItemDto(ItemMapper.toItem(itemService.update(owner.getId(), item.getId(), itemDto),
                        owner, itemRequest));
        assertNotNull(updatedItemDto);
        assertEquals(itemDto, updatedItemDto);
        verify(itemRepository).save(any(Item.class));
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void getById() {
        item.setComments(List.of(comment));
        currentBooking.setBooker(booker);
        futureBooking.setBooker(booker);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.getCurrentOrPastBookingByItem(item))
                .thenReturn(currentBooking);
        when(bookingRepository.getFutureBookingByItem(item))
                .thenReturn(futureBooking);
        ItemDtoForResponse itemDtoForResponse = itemService.getById(1L, 1L);
        assertNotNull(itemDtoForResponse);
        assertEquals(item, ItemMapper.toItem(itemDto, owner, itemRequest));
    }

    @Test
    void getAll() {
        item.setComments(List.of(comment));
        currentBooking.setBooker(booker);
        futureBooking.setBooker(booker);
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item));
        when(bookingRepository.getCurrentOrPastBookingByItem(item))
                .thenReturn(currentBooking);
        when(bookingRepository.getFutureBookingByItem(item))
                .thenReturn(futureBooking);
        Collection<ItemDtoForResponse> items = itemService.getAll(booker.getId());
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    void getByText() {
        String text = "text for search";
        when(itemRepository.search(text))
                .thenReturn(List.of(item));
        Collection<ItemDto> items = itemService.getByText(booker.getId(), text);
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    void getByTextEmpty() {
        String text = "";
        Collection<ItemDto> items = itemService.getByText(booker.getId(), text);
        assertNotNull(items);
        assertEquals(0, items.size());
    }

    @Test
    void addComment() {
        Page<Booking> bookings = new PageImpl<>(List.of(currentBooking));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(userService.getUser(anyLong()))
                .thenReturn(booker);
        when(bookingRepository.getAllPastByBookerOrderByStartDesc(booker, PageRequest.of(0, 1)))
                .thenReturn(bookings);
        CommentDtoForResponse savedComment = itemService.addComment(commentDto, item.getId(), booker.getId());
        assertNotNull(savedComment);
        assertEquals(comment.getText(), savedComment.getText());
        verify(commentRepository).save(any(Comment.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void getItem() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Item returnedItem = itemService.getItem(1L);
        assertNotNull(returnedItem);
        assertEquals(item, returnedItem);
    }

    @Test
    void validateIsUsersItem() {
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(anyLong()))
                .thenReturn(List.of(item));
        itemService.validateIsUsersItem(1L, 1L);
        assertDoesNotThrow(() -> { });
    }
}