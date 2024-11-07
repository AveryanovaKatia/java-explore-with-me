package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateCommentController {

    CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    public ResponseEntity<List<CommentResponseDto>> findAll(@PathVariable @Positive final Long userId,
                                                            @PathVariable @Positive final Long eventId,
                                                            @RequestParam(defaultValue = "0") final int from,
                                                            @RequestParam(defaultValue = "10") final int size) {
        final int page = from / size;
        return ResponseEntity.ok(commentService.findAll(userId, eventId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"))));
    }

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<CommentResponseDto> save(@RequestBody final CommentRequestDto commentRequestDto,
                                                   @PathVariable @Positive final Long userId,
                                                   @PathVariable @Positive final Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(commentRequestDto, userId, eventId));
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> update(@RequestBody final CommentRequestDto commentRequestDto,
                                                     @PathVariable @Positive final Long userId,
                                                     @PathVariable @Positive final Long commentId) {
        return ResponseEntity.ok(commentService.update(commentRequestDto, userId, commentId));
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> delete(@PathVariable @Positive final Long userId,
                                         @PathVariable @Positive final Long commentId) {
        commentService.delete(userId, commentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Комметарий удален: " + commentId);
    }
}
