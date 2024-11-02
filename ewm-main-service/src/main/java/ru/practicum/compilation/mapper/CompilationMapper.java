package ru.practicum.compilation.mapper;

import ru.practicum.compilation.dto.CompilationRequestDto;
import ru.practicum.compilation.dto.CompilationResponceDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.model.Event;

import java.util.List;

public class CompilationMapper {

    public static Compilation toCompilation(final CompilationRequestDto compilationRequestDto,
                                            final List<Event> evens) {

        final Compilation compilation = new Compilation();

        compilation.setEvents(evens);
        compilation.setPinned(compilationRequestDto.getPinned());
        compilation.setTitle(compilationRequestDto.getTitle());

        return compilation;
    }

    public static CompilationResponceDto toCompilationResponceDto(final Compilation compilation,
                                                           final List<EventResponseShortDto> eventResponseShortDtos) {

        final CompilationResponceDto compilationResponceDto = new CompilationResponceDto();

        compilationResponceDto.setId(compilation.getId());
        compilationResponceDto.setEvents(eventResponseShortDtos);
        compilationResponceDto.setPinned(compilation.getPinned());
        compilationResponceDto.setTitle(compilation.getTitle());

        return compilationResponceDto;
    }
}
