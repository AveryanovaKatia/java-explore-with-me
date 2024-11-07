package ru.practicum.comment.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;

import java.util.List;


public interface CommentService {

    //////////------------Private------------//////////

    List<CommentResponseDto> findAll(final Long userId,
                                     final Long eventId,
                                     final PageRequest pageRequest);

    CommentResponseDto save(final CommentRequestDto commentRequestDto,
                            final Long userId,
                            final Long eventId);

    CommentResponseDto update(final CommentRequestDto commentRequestDto,
                              final Long userId,
                              final Long commentId);

    void delete(final Long userId,
                final Long commentId);

    //////////------------Admin------------//////////

    void deleteByIds(final List<Long> ids);

    void deleteByEventId(final Long eventId);

    //////////------------Public------------//////////

    List<CommentResponseDto> findByEvent(final Long eventId,
                                         final PageRequest pageRequest);

    CommentResponseDto findById(final Long commentId);
}
