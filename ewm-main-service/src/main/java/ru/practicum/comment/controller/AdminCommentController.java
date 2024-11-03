package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCommentController {

    CommentService commentService;

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteByIds(@RequestParam final List<Long> ids) {
        commentService.deleteByIds(ids);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Комметарии удалены: " + ids);
    }

    @DeleteMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteByEventId(@PathVariable @Positive final Long eventId) {
        commentService.deleteByEventId(eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Удалены все комментарии у события с id: " + eventId);
    }
}
