package ru.practicum.request.mapper;

import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.model.Request;

public class RequestMapper {

    public static RequestDto toRequestDto(final Request request) {

        final RequestDto requestDto = new RequestDto();

        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setStatus(request.getStatus());
        requestDto.setCreated(request.getCreated());

        return requestDto;
    }
}
