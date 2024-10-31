package ru.practicum.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryResponseDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventResponseShortDto {

    String annotation;

    CategoryResponseDto category;

    Integer confirmedRequests;

    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;

    Boolean paid;

    String title;

    Integer views;
}
