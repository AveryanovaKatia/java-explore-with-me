package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicCommentController {

    CommentService commentService;

    @GetMapping("events/{eventId}/comments")
    public ResponseEntity<List<CommentResponseDto>> findByEvent(
                                    @PathVariable final Long eventId,
                                    @RequestParam(defaultValue = "0") final int from,
                                    @RequestParam(defaultValue = "10") final int size) {
        final int page = from / size;
        return ResponseEntity.ok(commentService.findByEvent(eventId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"))));
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> findById(@PathVariable @Positive final Long commentId) {
        return ResponseEntity.ok(commentService.findById(commentId));
    }
}
