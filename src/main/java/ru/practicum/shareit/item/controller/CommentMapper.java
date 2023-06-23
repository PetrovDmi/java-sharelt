package ru.practicum.shareit.item.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ModelMapper modelMapper = new ModelMapper();
    private final UserService userService;
    private final ItemService itemService;

    public CommentDto convertToDto(Comment comment) {
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        commentDto.setAuthorName(comment.getAuthor().getName());
        return commentDto;
    }

    public List<CommentDto> convertToDtoListOfComments(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            commentDtos.add(convertToDto(comment));
        }
        return commentDtos;
    }

    public Comment convertToEntity(CommentDto commentDto, long userId, long itemId) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        User author = userService.getUserById(userId);
        Item item = itemService.getItem(itemId);
        comment.setAuthor(author);
        comment.setItem(item);
        if (comment.getCreated() == null) {
            comment.setCreated(LocalDateTime.now());
        }
        return comment;
    }
}
