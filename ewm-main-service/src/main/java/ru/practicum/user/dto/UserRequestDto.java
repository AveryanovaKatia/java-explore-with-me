package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequestDto {

    @NotBlank
    @Size(min = 2, max = 250)
    String name;

    @NotBlank
    @Email(message = "Емейл должен содержать @ и наименование")
    @Size(min = 6, max = 254)
    String email;
}
