package ru.practicum.stat.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatDto;
import ru.practicum.StatResponseDto;
import ru.practicum.configuration.Formatter;
import ru.practicum.stat.client.StatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatsServiceImpl implements StatsService {

    private static final String APP_NAME = "ewm-main-service";

    DateTimeFormatter formatter = Formatter.getFormatter();

    StatsClient statsClient;

    @Override
    public void createStats(final String uri,
                            final String ip) {
        log.info("Запрос на отправку информации в сервис статистики для uri {}", uri);
        final StatDto statDto = new StatDto();
        statDto.setApp(APP_NAME);
        statDto.setIp(ip);
        statDto.setUri(uri);
        statDto.setTimestamp(LocalDateTime.now());
        final StatDto stat = statsClient.createStats(statDto);
        log.info("Информация сохранена {}", stat);
    }

    @Override
    public List<StatResponseDto> getStats(final List<Long> eventsId,
                                          final boolean unique) {
        log.info("Запрос на получение статистики с сервиса статистики для events {}", eventsId);
        final String start = LocalDateTime.now().minusYears(20).format(formatter);
        final String end = LocalDateTime.now().plusYears(20).format(formatter);
        final String[] uris = eventsId.stream()
                .map(id -> String.format("/events/%d", id))
                .toArray(String[]::new);
        return statsClient.getStats(start, end, uris, unique);
    }

    @Override
    public Map<Long, Long> getView(List<Long> eventsId, boolean unique) {
        log.info("Запрос на получение просмотров с сервиса статистики для events {}", eventsId);
        final List<StatResponseDto> stats = getStats(eventsId, unique);
        final Map<Long, Long> views = new HashMap<>();
        for (StatResponseDto stat : stats) {
            final Long id = Long.valueOf(stat.getUri().replace("/events/", ""));
            final Long view = stat.getHits();
            views.put(id, view);
        }
        log.info("Получены просмотры с сервиса статистики");
        return views;
    }
}