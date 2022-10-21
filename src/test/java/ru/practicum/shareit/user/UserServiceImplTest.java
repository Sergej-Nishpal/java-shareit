package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;
    private User userHaveId;
    private User savedUser;
    private User userHaveNotId;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);

        userHaveId = User.builder()
                .id(1L)
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();

        savedUser = User.builder()
                .id(1L)
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();

        userHaveNotId = User.builder()
                .id(null)
                .name("Sergej Nishpal")
                .email("sergej.nishpal@yandex.ru")
                .build();
    }

    @Test
    void findAll() {
        User userHaveIdTwo = User.builder()
                .id(3L)
                .name("John Doe")
                .email("john.doe@mail.com")
                .build();

        when(userRepository.findAll())
                .thenReturn(List.of(userHaveId, userHaveIdTwo));
        List<User> users = userService.findAll().stream()
                .map(UserMapper::toUser)
                .collect(Collectors.toList());
        assertEquals(2, users.size());
        assertEquals(userHaveId, users.get(0));
        assertEquals(userHaveIdTwo, users.get(1));
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findById() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.ofNullable(userHaveId));
        final UserDto responseUser = userService.getById(userHaveId.getId());
        assertEquals(userHaveId, UserMapper.toUser(responseUser));
        verify(userRepository).findById(userHaveId.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findByIdNotExists() {
        final Long userId = userHaveId.getId();
        final UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getById(userId));
        final String expectedMessage = userId.toString();
        final String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findByIdNotFound() {
        when(userRepository.findById(100L))
                .thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> userService.getById(100L));
        verify(userRepository).findById(100L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUser() {
        when(userRepository.save(any(User.class)))
                .thenReturn(userHaveId);
        User savedUser = UserMapper.toUser(userService.create(UserMapper.toUserDto(userHaveNotId)));
        assertNotNull(savedUser);
        assertEquals(userHaveId, savedUser);
        verify(userRepository).save(any(User.class));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createIfUserExists() {
        when(userRepository.existsById(anyLong()))
                .thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> userService.validateUserExists(1L));
        verify(userRepository).existsById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUser() {
        userHaveId.setEmail("s.nishpal@yandex.ru");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userHaveId));
        when(userRepository.save(any(User.class)))
                .thenReturn(userHaveId);

        User testResultUser = UserMapper
                .toUser(userService.update(userHaveId.getId(), UserMapper.toUserDto(userHaveId)));
        assertEquals(userHaveId, testResultUser);
        verify(userRepository).findById(userHaveId.getId());
        assertEquals(userHaveId, testResultUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserNull() {
        userHaveId.setId(33L);
        userHaveId.setName(null);
        userHaveId.setEmail(null);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User testResultUser = UserMapper
                .toUser(userService.update(userHaveId.getId(), UserMapper.toUserDto(userHaveId)));

        assertNotNull(testResultUser);
        assertNotEquals(userHaveId, testResultUser);
        verify(userRepository).findById(userHaveId.getId());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser() {
        userService.deleteById(1L);
        verify(userRepository).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }
}