package ru.practicum.event.contoller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseLongDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PrivateEventController {

    EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponseShortDto>> findAll(
                                               @PathVariable @Positive final Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                                               @RequestParam(defaultValue = "10") @Positive final int size) {
        final int page = from / size;
        return ResponseEntity.ok(eventService
                .findAll(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<EventResponseLongDto> save(
                                               @PathVariable @Positive final Long userId,
                                               @RequestBody @Valid final EventRequestDto eventRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.save(userId, eventRequestDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseLongDto> findById(
                                               @PathVariable @Positive final Long userId,
                                               @PathVariable @Positive final Long eventId) {
        return ResponseEntity.ok(eventService.findById(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponseLongDto> update(
                                               @PathVariable @Positive final Long userId,
                                               @PathVariable @Positive final Long eventId,
                                               @RequestBody @Valid final EventUpdateDto eventUpdateDto) {
        return ResponseEntity.ok(eventService.update(userId, eventId, eventUpdateDto));
    }

    //////////------------Requests------------//////////

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<RequestDto>> findRequestsByEventId(@PathVariable final Long userId,
                                                                  @PathVariable final Long eventId) {
        return ResponseEntity.ok(eventService.findRequestsByEventId(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<Map<String, List<RequestDto>>> approveRequests(
                                                @PathVariable final Long userId,
                                                @PathVariable final Long eventId,
                                                @RequestBody @Valid final RequestUpdateDto requestUpdateDto) {
        return ResponseEntity.ok(eventService.approveRequests(userId, eventId, requestUpdateDto));
    }
}
