package ru.practicum.comment.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;

    UserRepository userRepository;

    EventRepository eventRepository;

    //////////------------Private------------//////////

    @Override
    public List<CommentResponseDto> findAll(final Long userId,
                                            final Long eventId,
                                            final PageRequest pageRequest) {
        log.info("Запрос на получение всех комментариев пользователя с id = {}", userId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        final List<Comment> comments = commentRepository.findByAuthorAndEvent(user, event, pageRequest);
        if (comments.isEmpty()) {
            log.info("Пользователь с id = {} еще не написал ни одного комментария к событию  с id = {}",
                    userId, eventId);
            return new ArrayList<>();
        }
        log.info("Получены все комментарии пользователя  с id = {} к событию с id = {}",
                userId, eventId);
        return comments.stream().map(CommentMapper::toCommentResponseDto).toList();
    }

    @Override
    public CommentResponseDto save(final CommentRequestDto commentRequestDto,
                                   final Long userId,
                                   final Long eventId) {
        log.info("Запрос на сохранение комментария к событию с id = {}", eventId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя написать комментарий к событию которое еще не было опубликованно");
        }
        final Comment comment = commentRepository.save(CommentMapper.toComment(commentRequestDto, user, event));
        log.info("Комментарий к событию с id = {} успешно сохранен", eventId);
        return CommentMapper.toCommentResponseDto(comment);
    }

    @Override
    public CommentResponseDto update(final CommentRequestDto commentRequestDto,
                                     final Long userId,
                                     final Long commentId) {
        log.info("Запрос на обновление комментария с id = {}", commentId);
        final Comment oldComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с id = {} нет." + commentId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        if (!oldComment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Редактировать комментарии разрешено только его автору");
        }
        oldComment.setText(commentRequestDto.getText());
        final Comment comment = commentRepository.save(oldComment);
        log.info("Комментарий с id = {} успешно обновлен", commentId);
        return CommentMapper.toCommentResponseDto(comment);
    }

    @Override
    public void delete(final Long userId,
                       final Long commentId) {
        log.info("Запрос на удаление комментария с id = {}", commentId);
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с id = {} нет." + commentId));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        if (!comment.getAuthor().getId().equals(userId) &&
                !comment.getAuthor().getId().equals(comment.getEvent().getInitiator().getId())) {
            throw new ConflictException("Удалять комментарии разрешено только его автору или инициатору мероприятия");
        }
        commentRepository.deleteById(commentId);
        log.info("Комментарий с id = {} успешно удален", commentId);
    }

    //////////------------Admin------------//////////

    @Override
    public void deleteByIds(final List<Long> ids) {
        log.info("Запрос на удаление комментариев администратором");
        final List<Event> events = eventRepository.findAllById(ids);
        if (ids.size() != events.size()) {
            throw new ValidationException("Были переданны несуществующие id событий");
        }
        commentRepository.deleteAllById(ids);
        log.info("Комментарии успешно удалены");
    }

    @Override
    public void deleteByEventId(final Long eventId) {
        log.info("Запрос на удаление всех комментариев у события с id = {}", eventId);
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        commentRepository.deleteByEvent(event);
        log.info("Все комментарии у события с id = {} успешно удалены", eventId);
    }

    //////////------------Public------------//////////

    @Override
    public List<CommentResponseDto> findByEvent(final Long eventId,
                                                final PageRequest pageRequest) {
        log.info("Запрос на получение всех комментариев у события с id = {}", eventId);
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        final List<Comment> comments = commentRepository.findByEvent(event, pageRequest);
        if (comments.isEmpty()) {
            log.info("У события с id = {} еще нет комментариев", eventId);
            return new ArrayList<>();
        }
        log.info("Получены все комментарии события с id = {}", eventId);
        return comments.stream().map(CommentMapper::toCommentResponseDto).toList();
    }

    @Override
    public CommentResponseDto findById(final Long commentId) {
        log.info("Запрос на получение комментария с id = {}", commentId);
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментария с id = {} нет." + commentId));
        log.info("Комментарий с id = {} успешно получен", commentId);
        return CommentMapper.toCommentResponseDto(comment);
    }
}
