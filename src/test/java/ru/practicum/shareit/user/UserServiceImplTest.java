package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

    @Captor
    private ArgumentCaptor<User> captor;
    private User userHaveId;
    private User userHaveNotId;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);

        userHaveId = User.builder()
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
        User updatedUser = userHaveId;
        when(userRepository.findById(userHaveId.getId())).thenReturn(Optional.of(userHaveId));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);
        User testResultUser = UserMapper
                .toUser(userService.update(updatedUser.getId(), UserMapper.toUserDto(updatedUser)));
        assertEquals(updatedUser, testResultUser);
        verify(userRepository).findById(userHaveId.getId());
        verify(userRepository).save(captor.capture());
        User capturedUser = captor.getValue();
        assertEquals(updatedUser, capturedUser);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteUser() {
        userService.deleteById(1L);
        verify(userRepository).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }
}