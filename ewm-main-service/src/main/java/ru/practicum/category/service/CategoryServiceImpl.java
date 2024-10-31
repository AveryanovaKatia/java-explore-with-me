package ru.practicum.category.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryRequestDto;
import ru.practicum.category.dto.CategoryResponseDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    EventRepository eventRepository;

    //////////------------Admin------------//////////

    @Override
    public CategoryResponseDto save(final CategoryRequestDto categoryRequestDto) {
        log.info("Запрос на добавление категории");
        try {
            final Category category = categoryRepository.save(CategoryMapper.toCategory(categoryRequestDto));
            log.info("Категория успешно добавлена под id {}", category.getId());
            return CategoryMapper.toCategoryResponseDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Категория с таким названием уже есть");
        }
    }

    @Override
    public void delete(final Long catId) {
        log.info("Запрос на удаление категории с id {}", catId);
        final Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с id = {} нет." + catId));

        final List<Event> events = eventRepository.findByCategoryId(catId);
        if (!events.isEmpty()) {
            throw new ConflictException("Нельзя удалить категорию, с которой связаны события");
        }
        categoryRepository.delete(category);
        log.info("Пользователь с id {} успешно удален ", catId);
    }

    @Override
    public CategoryResponseDto update(final CategoryRequestDto categoryRequestDto,
                                      final Long catId) {
        log.info("Запрос на обновление категории с id {}", catId);
        final Category oldCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с id = {} нет." + catId));
        try {
            oldCategory.setName(categoryRequestDto.getName());
            final Category category = categoryRepository.save(oldCategory);
            return CategoryMapper.toCategoryResponseDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Нельзя изменить название категории на уже существующее под другим id");
        }
    }

    //////////------------Public------------//////////

    @Override
    public List<CategoryResponseDto> findAll(final PageRequest pageRequest) {
        log.info("Запрос на получение всех категорий");
        final List<Category> categories = categoryRepository.findAll(pageRequest).getContent();
        if (categories.isEmpty()) {
            log.info("Список категорий пока пуст");
            return new ArrayList<>();
        }
        log.info("Список категорий:");
        return categories.stream()
                .map(CategoryMapper::toCategoryResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto findById(final Long catId) {
        log.info("Запрос на получение категории с id {}", catId);
        final Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категории с id = {} нет." + catId));
        return CategoryMapper.toCategoryResponseDto(category);
    }
}
