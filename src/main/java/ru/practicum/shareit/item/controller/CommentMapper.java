package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final ModelMapper modelMapper = new ModelMapper();
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

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
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с ID " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Товар с ID " + itemId + " не найден"));
        comment.setAuthor(author);
        comment.setItem(item);
        if (comment.getCreated() == null) {
            comment.setCreated(LocalDateTime.now());
        }
        return comment;
    }
}
