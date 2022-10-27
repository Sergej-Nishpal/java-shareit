package ru.practicum.shareit.item.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class CommentDtoForResponse {

    private Long id;
    private String text;
    private String itemName;
    private String authorName;
    private LocalDateTime created;
}