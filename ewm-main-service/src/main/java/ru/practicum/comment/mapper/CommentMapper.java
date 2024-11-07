package ru.practicum.comment.mapper;

import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(final CommentRequestDto commentRequestDto,
                                    final User user,
                                    final Event event) {

        final Comment comment = new Comment();

        comment.setText(commentRequestDto.getText());
        comment.setCreated(LocalDateTime.now());
        comment.setAuthor(user);
        comment.setEvent(event);

        return comment;
    }

    public static CommentResponseDto toCommentResponseDto(final Comment comment) {

        final CommentResponseDto commentResponseDto = new CommentResponseDto();

        commentResponseDto.setId(comment.getId());
        commentResponseDto.setText(comment.getText());
        commentResponseDto.setAuthorName(comment.getAuthor().getName());
        commentResponseDto.setEvent(EventMapper.toEventResponseShortDto(comment.getEvent()));
        commentResponseDto.setCreated(comment.getCreated());

        return commentResponseDto;
    }
}
