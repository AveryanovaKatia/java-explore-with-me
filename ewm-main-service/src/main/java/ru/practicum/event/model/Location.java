package ru.practicum.event.model;

import jakarta.validation.constraints.Pattern;

public record Location(@Pattern(regexp = "^-?([0-8]?\\d(\\.\\d{1,6})?|90)$",
                                     message = "Значение широты выходит из диапазона -90 и 90") Double lat,
                       @Pattern(regexp = "^-?(?:1(?:[0-7]\\d(\\.\\d{1,6})?|80)|([1-9]?\\d(\\.\\d{1,6})?))$",
                                     message = "Значение долготы выходит из диапазона -180 и 180") Double lon) {
}
