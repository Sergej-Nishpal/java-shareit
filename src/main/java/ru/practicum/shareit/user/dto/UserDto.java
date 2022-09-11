package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserDto {
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    @Email(message = "Указан некорректный email!")
    private String email;
}
