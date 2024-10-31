package ru.practicum.event.contoller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.configuration.Formatter;
import ru.practicum.event.dto.EventResponseLongDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicEventController {

    EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponseShortDto>> findAllPublic(
                               @RequestParam(defaultValue = "") final String text,
                               @RequestParam(required = false) final List<Long> categories,
                               @RequestParam(required = false) final Boolean paid,
                               @RequestParam(required = false) final String rangeStart,
                               @RequestParam(required = false) final String rangeEnd,
                               @RequestParam(defaultValue = "false") final boolean onlyAvailable,
                               @RequestParam(required = false) final EventSort sort,
                               @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                               @RequestParam(defaultValue = "10") @Positive final int size,
                               final HttpServletRequest request) {
        final LocalDateTime start = (Objects.nonNull(rangeStart)) ?
                LocalDateTime.parse(rangeStart, Formatter.getFormatter()) : LocalDateTime.now();
        final LocalDateTime end = (Objects.nonNull(rangeEnd)) ?
                LocalDateTime.parse(rangeEnd, Formatter.getFormatter()) : LocalDateTime.now().plusYears(20);
        final int page = from / size;
        return ResponseEntity.ok(eventService.findAllPublic(text, categories, paid, start, end, onlyAvailable, sort,
                PageRequest.of(page, size), request, from));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseLongDto> getByIdPublic(@PathVariable final Long eventId,
                                                              final HttpServletRequest request) {
        return ResponseEntity.ok(eventService.getByIdPublic(eventId, request));
    }
}
