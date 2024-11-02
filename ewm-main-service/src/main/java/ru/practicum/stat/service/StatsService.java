package ru.practicum.stat.service;

import ru.practicum.StatResponseDto;

import java.util.List;
import java.util.Map;

public interface StatsService {

    void createStats(final String uri,
                     final String ip);

    List<StatResponseDto> getStats(final List<Long> eventsId,
                                   final boolean unique);

    Map<Long, Long> getView(final List<Long> eventsId,
                            final boolean unique);
}