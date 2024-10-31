package ru.practicum.user.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;


@Data
@EqualsAndHashCode(exclude = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDto {

    Long id;

    String name;

    String email;
}
