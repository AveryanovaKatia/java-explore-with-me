package ru.practicum.event.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryResponseDto;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventResponseLongDto {

    String annotation;

    CategoryResponseDto category;

    Integer confirmedRequests;

    LocalDateTime createdOn;

    String description;

    LocalDateTime eventDate;

    Long id;

    UserShortDto initiator;

    Location location;

    Boolean paid;

    Integer participantLimit;

    LocalDateTime publishedOn;

    Boolean requestModeration;

    EventState state;

    String title;

    Integer views;
}
