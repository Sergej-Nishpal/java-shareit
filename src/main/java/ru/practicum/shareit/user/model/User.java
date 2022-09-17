package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {
    private Long id;
    private String name;

    @Email(message = "Указан некорректный email!")
    private String email;
}