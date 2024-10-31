package ru.practicum.stat.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.StatDto;
import ru.practicum.StatResponseDto;

import java.util.List;

@FeignClient(value = "stats-client", url = "http://stats-server:9090")
public interface StatsClient {

    @PostMapping("/hit")
    StatDto createStats(@RequestBody StatDto creationDto);

    @GetMapping("/stats")
    List<StatResponseDto> getStats(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) String[] uris,
                                   @RequestParam(defaultValue = "false") boolean unique);
}