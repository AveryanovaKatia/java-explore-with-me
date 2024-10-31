package ru.practicum.category.contoller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
import ru.practicum.category.dto.CategoryRequestDto;
import ru.practicum.category.dto.CategoryResponseDto;
import ru.practicum.category.service.CategoryService;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryController {

    CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CategoryResponseDto> save(@Valid @RequestBody final CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(categoryRequestDto));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> delete(@PathVariable final Long catId) {
        categoryService.delete(catId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Категория удалена: " + catId);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryResponseDto> update(@Valid @RequestBody final CategoryRequestDto categoryRequestDto,
                                                      @PathVariable @Positive final Long catId) {
        return ResponseEntity.ok(categoryService.update(categoryRequestDto, catId));
    }
}
