package ru.practicum.category.contoller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.dto.CategoryResponseDto;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicCategoryController {

    CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> findAll(
                         @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
                         @RequestParam(defaultValue = "10") @Positive final int size) {
        final int page = from / size;
        return ResponseEntity.ok(categoryService
                .findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryResponseDto> findById(@PathVariable @Positive final Long catId) {
        return ResponseEntity.ok(categoryService.findById(catId));
    }
}
