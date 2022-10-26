package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    UserDto userDtoOne;
    UserDto userDtoTwo;

    @BeforeEach
    void setUp() {
        userDtoOne = UserDto.builder()
                .name("Sergej")
                .email("sergej.nishpal@yandex.ru")
                .build();

        userDtoTwo = UserDto.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();
    }

    @Test
    void createUser(@Autowired MockMvc mvc) throws Exception {
        when(userService.create(userDtoOne))
                .thenReturn(userDtoOne);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOne.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOne.getEmail())));
    }

    @Test
    void updateUser(@Autowired MockMvc mvc) throws Exception {
        when(userService.update(anyLong(), any()))
                .thenReturn(userDtoTwo);

        mvc.perform(patch("/users/2")
                        .content(mapper.writeValueAsString(userDtoTwo))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoTwo.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoTwo.getName())))
                .andExpect(jsonPath("$.email", is(userDtoTwo.getEmail())));
    }

    @Test
    void findAll(@Autowired MockMvc mvc) throws Exception {
        Collection<UserDto> users = List.of(userDtoOne, userDtoTwo);
        when(userService.findAll())
                .thenReturn(users);

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(userDtoOne.getName())))
                .andExpect(jsonPath("$[1].name", is(userDtoTwo.getName())));
    }

    @Test
    void getById(@Autowired MockMvc mvc) throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(userDtoOne);

        mvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDtoOne))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoOne.getName())))
                .andExpect(jsonPath("$.email", is(userDtoOne.getEmail())));
    }

    @Test
    void deleteById(@Autowired MockMvc mvc) throws Exception {
        userDtoOne.setId(1L);
        mvc.perform(delete("/users/{id}", userDtoOne.getId()))
                .andExpect(status().isOk());
    }
}