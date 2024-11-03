package ru.practicum.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteByEvent(final Event event);

    List<Comment> findByEvent(final Event event,
                              final PageRequest pageRequest);

    List<Comment> findByAuthorAndEvent(final User author,
                                     final Event event,
                                     final PageRequest pageRequest);
}