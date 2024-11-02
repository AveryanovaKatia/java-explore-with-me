package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.CreateGroup;
import ru.practicum.StatDto;
import ru.practicum.StatResponseDto;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatServiceController {

    StatService statService;

    static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    public ResponseEntity<StatDto> saveStatEvent(@RequestBody @Validated(CreateGroup.class) final StatDto statDto) {
        return new ResponseEntity<>(statService.saveStat(statDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatResponseDto>> readStatEvent(
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) final LocalDateTime end,
            @RequestParam(defaultValue = "") final List<String> uris,
            @RequestParam(defaultValue = "false") final boolean unique) {
        return new ResponseEntity<>(statService.readStat(start, end, uris, unique), HttpStatus.OK);
    }
}