package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatDto;
import ru.practicum.StatResponseDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.StatMapper;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatServiceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatServiceImpl implements StatService {

    StatServiceRepository statServiceRepository;

    StatMapper statMapper;

    @Override
    @Transactional
    public StatDto saveStat(final StatDto statDto) {
        log.info("Запрос на создание элемента для статистики");
        final Stat stat = statServiceRepository.save(statMapper.toStat(statDto));
        log.info("Элемент статистики успешно создан");
        return statMapper.toStatDto(stat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatResponseDto> readStat(final LocalDateTime start,
                                          final LocalDateTime end,
                                          final List<String> uris,
                                          final boolean unique) {
        log.info("Запрос на получение списка статистических данных");

        if (start.isAfter(end)) {
            throw new ValidationException("Дата начала не может быть позже даты окончания");
        }

        if (uris.isEmpty() && unique) {
            log.info("Получаем список всех уникальных элементов статистики");
            return statServiceRepository.findAllWithUniqueIp(start, end);
        } else if (uris.isEmpty()) {
            log.info("Получаем список вообще всех элементов статистики");
            return statServiceRepository.findAllWithCount(start, end);
        } else if (unique) {
            log.info("Получаем список уникальных элементов статистики для определенных uri");
            return statServiceRepository.findAllWithUrisUniqueIp(start, end, uris);
        } else {
            log.info("Получаем список не уникальных элементов статистики для определенных uri");
            return statServiceRepository.findAllWithUris(start, end, uris);
        }
    }
}

