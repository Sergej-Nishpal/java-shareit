package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "description")
public class ItemRequestDto {
    private String description;
}