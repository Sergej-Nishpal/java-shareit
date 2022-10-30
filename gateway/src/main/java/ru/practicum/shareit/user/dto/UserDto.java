package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserDto {

    @Null(groups = Marker.OnCreate.class)
    @Min(groups = Marker.OnUpdate.class, value = 1L)
    private Long id;

    @NotBlank(groups = Marker.OnCreate.class)
    private String name;

    @NotBlank(groups = Marker.OnCreate.class)
    @Email(message = "Указан некорректный email!")
    private String email;
}