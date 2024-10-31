package ru.practicum.compilation.contoller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationResponceDto;
import ru.practicum.compilation.service.CompilationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicCompilationController {

    CompilationServiceImpl compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationResponceDto> getAll(
                           @RequestParam(required = false) final Boolean pinned,
                           @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                           @RequestParam(defaultValue = "10") @Positive final int size) {
        final int page = from / size;
        final Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return compilationService.getAll(pinned, PageRequest.of(page, size, sort));
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponceDto getById(@PathVariable final Long compId) {
        return compilationService.getById(compId);
    }
}