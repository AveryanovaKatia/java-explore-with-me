package ru.practicum.compilation.contoller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.compilation.dto.CompilationRequestDto;
import ru.practicum.compilation.dto.CompilationResponceDto;
import ru.practicum.compilation.dto.CompilationUpdateDto;
import ru.practicum.compilation.service.CompilationService;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCompilationController {

    CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CompilationResponceDto> save(
                                    @Valid @RequestBody final CompilationRequestDto compilationRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.save(compilationRequestDto));
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> delete(@PathVariable final Long compId) {
        compilationService.delete(compId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Подборка удалена: " + compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CompilationResponceDto> update(
                                     @Valid @RequestBody CompilationUpdateDto compilationUpdateDto,
                                     @PathVariable final Long compId) {
        return ResponseEntity.ok(compilationService.update(compilationUpdateDto, compId));
    }
}
