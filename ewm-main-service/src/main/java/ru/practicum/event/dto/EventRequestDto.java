package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull
    Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @NotNull
    @Future
    LocalDateTime eventDate;

    @NotNull
    Location location;

    @JsonSetter(nulls = Nulls.SKIP)
    Boolean paid = false;

    @PositiveOrZero
    @JsonSetter(nulls = Nulls.SKIP)
    Integer participantLimit = 0;

    @JsonSetter(nulls = Nulls.SKIP)
    Boolean requestModeration = true;

    EventState state;

    @NotBlank
    @Size(min = 3, max = 120)
    String title;
}