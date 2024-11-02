package ru.practicum.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.category.dto.CategoryRequestDto;
import ru.practicum.category.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    //////////------------Admin------------//////////

    CategoryResponseDto save(final CategoryRequestDto categoryRequestDto);

    void delete(final Long catId);

    CategoryResponseDto update(final CategoryRequestDto categoryRequestDto,
                               final Long catId);

    //////////------------Public------------//////////

    List<CategoryResponseDto> findAll(final PageRequest pageRequest);

    CategoryResponseDto findById(final Long catId);
}
