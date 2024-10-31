package ru.practicum.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.compilation.dto.CompilationRequestDto;
import ru.practicum.compilation.dto.CompilationResponceDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {

    //////////------------Admin------------//////////

    CompilationResponceDto save(final CompilationRequestDto compilationRequestDto);

    void delete(final Long compId);

    CompilationResponceDto update(final CompilationUpdateDto compilationUpdateDto,
                                  final Long compId);

    //////////------------Public------------//////////

    List<CompilationResponceDto> getAll(final Boolean pinned,
                                        final Pageable pageable);

    CompilationResponceDto getById(final Long compId);
}
