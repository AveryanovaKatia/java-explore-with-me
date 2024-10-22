package ru.practicum;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatDto {

    @NotBlank(groups = {CreateGroup.class})
    @Size(max = 255)
    String app;

    @NotBlank(groups = {CreateGroup.class})
    @Size(max = 255)
    String uri;

    @NotBlank(groups = {CreateGroup.class})
    @Size(max = 255)
    String ip;

    @NotNull(groups = {CreateGroup.class})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    LocalDateTime timestamp;
}
