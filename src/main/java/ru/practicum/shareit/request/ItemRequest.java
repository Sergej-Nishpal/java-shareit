package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemRequest {
    private Long id;

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    private LocalDateTime created;
}