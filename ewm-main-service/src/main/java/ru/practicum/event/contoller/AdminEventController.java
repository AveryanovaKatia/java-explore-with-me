package ru.practicum.event.contoller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.configuration.Formatter;
import ru.practicum.event.dto.EventResponseLongDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminEventController {

    EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponseLongDto>> findOnParameters(
                                            @RequestParam(required = false) final List<Long> users,
                                            @RequestParam(required = false) final List<String> states,
                                            @RequestParam(required = false) final List<Long> categories,
                                            @RequestParam(required = false) final String rangeStart,
                                            @RequestParam(required = false) final String rangeEnd,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                            @RequestParam(defaultValue = "10") @Positive final int size) {
        final int page = from / size;
        final LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, Formatter.getFormatter())
                : LocalDateTime.now();
        final LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, Formatter.getFormatter())
                : LocalDateTime.now().plusYears(20);

        return ResponseEntity.ok(eventService.findOnParameters(users, states, categories, start, end,
                        PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponseLongDto> approveEventByAdmin(
                                             @PathVariable @Positive final Long eventId,
                                             @RequestBody @Valid final EventUpdateDto eventUpdateDto) {
        return ResponseEntity.ok(eventService.approveEventByAdmin(eventId, eventUpdateDto));
    }
}
