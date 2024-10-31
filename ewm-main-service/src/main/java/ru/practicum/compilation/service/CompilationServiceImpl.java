package ru.practicum.compilation.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationRequestDto;
import ru.practicum.compilation.dto.CompilationResponceDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompilationServiceImpl implements CompilationService {

    CompilationRepository compilationRepository;

    EventRepository eventRepository;

    //////////------------Admin------------//////////

    @Override
    public CompilationResponceDto save(final CompilationRequestDto compilationRequestDto) {
        log.info("Запрос на сохранение подборки администратором");
        final List<Event> events = eventRepository.findByIdIn(compilationRequestDto.getEvents());
        final Compilation compilation = CompilationMapper.toCompilation(compilationRequestDto, events);
        if (Objects.isNull(compilationRequestDto.getPinned())) {
            compilation.setPinned(false);
        }
        final Compilation newCompilation = compilationRepository.save(compilation);
        log.info("подборка успешно сохранена администратором");
        return CompilationMapper.toCompilationResponceDto(newCompilation, events.stream()
                .map(EventMapper::toEventResponseShortDto)
                .toList());
    }

    @Override
    public void delete(final Long compId) {
        log.info("Запрос на удаление подборки администратором с id = {}", compId);
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id = {} нет." + compId));
        compilationRepository.deleteById(compId);
        log.info("Подборка с id = {} успешно удалена администратором", compId);
    }

    @Override
    public CompilationResponceDto update(final CompilationUpdateDto compilationUpdateDto,
                                         final Long compId) {
        log.info("Запрос на обновление данных подборки с id = {}", compId);
        final Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id = {} нет." + compId));
        final List<Event> events = eventRepository.findByIdIn(compilationUpdateDto.getEvents());
        if (Objects.nonNull(compilationUpdateDto.getEvents())) {
            compilation.setEvents(events);
        }
        if (Objects.nonNull(compilationUpdateDto.getPinned())) {
            compilation.setPinned(compilationUpdateDto.getPinned());
        }
        if (Objects.nonNull(compilationUpdateDto.getTitle())) {
            compilation.setTitle(compilationUpdateDto.getTitle());
        }
        final Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Успешное обновление данных подборки с id = {}", compId);
       return CompilationMapper.toCompilationResponceDto(newCompilation, events.stream()
                .map(EventMapper::toEventResponseShortDto)
                .toList());
    }

    //////////------------Public------------//////////

    @Override
    public List<CompilationResponceDto> getAll(final Boolean pinned,
                                               final Pageable pageable) {
        log.info("Запрос на получение списка всех подборок");
        List<Compilation> compilationList;
        if (Objects.nonNull(pinned)) {
            compilationList = compilationRepository.findByPinned(pinned, pageable);
        } else {
            compilationList = compilationRepository.findAll(pageable).toList();
        }
        if (compilationList.isEmpty()) {
            log.info("Еще не создано ни одной подборки");
            return new ArrayList<>();
        }
        log.info("Найден список подборок");
        return compilationList.stream()
                .map(c -> CompilationMapper.toCompilationResponceDto(c, c.getEvents().stream()
                        .map(EventMapper::toEventResponseShortDto).toList())).toList();
    }

    @Override
    public CompilationResponceDto getById(final Long compId) {
        log.info("Запрос на получение данных подборки с id = {}", compId);
        final Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборки с id = {} нет." + compId));
        log.info("Подборка с id = {} успешно получена", compId);
        return CompilationMapper.toCompilationResponceDto(compilation, compilation.getEvents().stream()
                .map(EventMapper::toEventResponseShortDto)
                .toList());
    }
}
