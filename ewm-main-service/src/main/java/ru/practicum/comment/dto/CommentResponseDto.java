package ru.practicum.comment.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventResponseShortDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentResponseDto {

    Long id;

    String text;

    String authorName;

    EventResponseShortDto event;

    LocalDateTime created;
}
