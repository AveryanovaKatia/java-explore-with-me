package ru.practicum.request.service;

import ru.practicum.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    List<RequestDto> findAll(final Long userId);

    RequestDto save(final Long userId,
                    final Long eventId);

    RequestDto cancelRequest(final Long userId,
                             final Long requestId);
}
