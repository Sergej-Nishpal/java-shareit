package ru.practicum.shareit.request.dto;

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
public class ItemRequestDto {

    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    private LocalDateTime created;
}
