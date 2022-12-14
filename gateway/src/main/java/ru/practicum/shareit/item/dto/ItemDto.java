package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.validation.Marker;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemDto {

    @Null(groups = Marker.OnCreate.class)
    @Min(groups = Marker.OnUpdate.class, value = 1L)
    private Long id;

    @NotBlank(groups = Marker.OnCreate.class)
    private String name;

    @NotBlank(groups = Marker.OnCreate.class)
    private String description;

    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;

    private Long requestId;
}