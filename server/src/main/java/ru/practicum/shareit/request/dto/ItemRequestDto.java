package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "description")
public class ItemRequestDto {

    @NotNull
    private String description;
}