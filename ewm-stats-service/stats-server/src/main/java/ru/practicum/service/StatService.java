package ru.practicum.service;

import ru.practicum.StatDto;
import ru.practicum.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    StatDto saveStat(final StatDto statDto);

    List<StatResponseDto> readStat(final LocalDateTime start,
                                   final LocalDateTime end,
                                   final List<String> uris,
                                   final boolean unique);
}