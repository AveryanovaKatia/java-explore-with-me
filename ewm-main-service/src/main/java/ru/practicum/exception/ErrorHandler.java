package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.category.contoller.AdminCategoryController;
import ru.practicum.category.contoller.PublicCategoryController;
import ru.practicum.compilation.contoller.AdminCompilationController;
import ru.practicum.compilation.contoller.PublicCompilationController;
import ru.practicum.event.contoller.AdminEventController;
import ru.practicum.event.contoller.PrivateEventController;
import ru.practicum.event.contoller.PublicEventController;
import ru.practicum.request.contoller.RequestController;
import ru.practicum.user.contoller.UserController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        UserController.class,
        AdminCategoryController.class,
        PublicCategoryController.class,
        PrivateEventController.class,
        AdminEventController.class,
        PublicEventController.class,
        RequestController.class,
        PublicCompilationController.class,
        AdminCompilationController.class})
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValid(final MethodArgumentNotValidException e) {
        log.error("Исключение {}: {}", e, e.getMessage());
        final List<String> details = new ArrayList<>();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        return createMap("BAD_REQUEST", "Неправильно составлен запрос",
                String.join(", ", details));
    }

    @ExceptionHandler({MissingServletRequestParameterException.class,
                       MethodArgumentTypeMismatchException.class,
                       ValidationException.class,
                       MissingRequestHeaderException.class,
                       BadRequestException.class,
                       ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValid(final Exception e) {
        log.error("Исключение, {}: {}", e, e.getMessage());
        return createMap("BAD_REQUEST", "Неправильно составлен запрос", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException exception) {
        log.debug("Получен статус 404 NOT_FOUND{}", exception.getMessage(), exception);
        return createMap("NOT_FOUND", "Объект не найден", exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handlerInternal(final Exception exception) {
        log.debug("Получен статус 500 INTERNAL_SERVER_ERROR {}", exception.getMessage(), exception);
        return createMap("INTERNAL_SERVER_ERROR", "Ошибка", exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlerConflict(final ConflictException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return createMap("CONFLICT", "Нарушено ограничение целостности", exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handlerForbidden(final ForbiddenException exception) {
        log.debug("Получен статус 403 FORBIDDEN {}", exception.getMessage(), exception);
        return createMap("FORBIDDEN","Для запрошенной операции условия не выполнены",
                exception.getMessage());
    }

    private Map<String, String> createMap(String status, String reason, String message) {
        return Map.of("status", status,
                "reason", reason,
                "message", message,
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
