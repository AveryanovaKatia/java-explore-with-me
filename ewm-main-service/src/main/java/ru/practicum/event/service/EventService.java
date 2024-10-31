package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseLongDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.model.EventSort;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {

    //////////------------Private------------//////////

    List<EventResponseShortDto> findAll(final Long userId,
                                        final Pageable pageable);

    EventResponseLongDto save(final Long userId,
                              final EventRequestDto eventRequestDto);

    EventResponseLongDto findById(final Long userId,
                                  final Long eventId);

    EventResponseLongDto update(final Long userId,
                                final Long eventId,
                                final EventUpdateDto eventUpdateDto);

    List<RequestDto> findRequestsByEventId(final Long userId,
                                          final Long eventId);

    Map<String, List<RequestDto>> approveRequests(final Long userId,
                                                  final Long eventId,
                                                  final RequestUpdateDto requestUpdateDto);

    //////////------------Admin------------//////////

    List<EventResponseLongDto> findOnParameters(final List<Long> usersId,
                                                final List<String> states,
                                                final List<Long> categoriesId,
                                                final LocalDateTime start,
                                                final LocalDateTime end,
                                                final Pageable pageable);

    EventResponseLongDto approveEventByAdmin(final Long eventId,
                                             final EventUpdateDto eventUpdateDto);

    //////////------------Public------------//////////

    List<EventResponseShortDto> findAllPublic(final String text,
                                              final List<Long> categories,
                                              final Boolean paid,
                                              final LocalDateTime start,
                                              final LocalDateTime end,
                                              final boolean onlyAvailable,
                                              final EventSort sort,
                                              final PageRequest pageRequest,
                                              final HttpServletRequest request,
                                              final int from);

    EventResponseLongDto getByIdPublic(final Long eventId,
                                       final HttpServletRequest request);
}
