package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        return Comment.builder()
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDtoForResponse toCommentDtoForResponse(Comment comment) {
        return CommentDtoForResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemName(comment.getItem().getName())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}