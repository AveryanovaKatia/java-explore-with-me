package ru.practicum.compilation.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventResponseShortDto;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationResponceDto {

    Long id;

    List<EventResponseShortDto> events;

    Boolean pinned;

    String title;
}
