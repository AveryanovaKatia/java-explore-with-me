package ru.practicum.event.mapper;

import ru.practicum.category.dto.CategoryResponseDto;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseLongDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.EventState;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toEvent(final EventRequestDto eventRequestDto,
                                final User initiator,
                                final Category category) {

        final Event event = new Event();

        event.setInitiator(initiator);
        event.setAnnotation(eventRequestDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(eventRequestDto.getDescription());
        event.setEventDate(eventRequestDto.getEventDate());
        event.setLat(eventRequestDto.getLocation().lat());
        event.setLon(eventRequestDto.getLocation().lon());
        event.setPaid(eventRequestDto.getPaid());
        event.setParticipantLimit(eventRequestDto.getParticipantLimit());
        event.setRequestModeration(eventRequestDto.getRequestModeration());
        event.setTitle(eventRequestDto.getTitle());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setConfirmedRequests(0);

        return event;
    }

    public static EventResponseShortDto toEventResponseShortDto(final Event event) {

        final EventResponseShortDto eventResponseShortDto = new EventResponseShortDto();

        eventResponseShortDto.setAnnotation(event.getAnnotation());
        eventResponseShortDto.setCategory(
                new CategoryResponseDto(event.getCategory().getId(), event.getCategory().getName()));
        eventResponseShortDto.setConfirmedRequests(event.getConfirmedRequests());
        eventResponseShortDto.setEventDate(event.getEventDate());
        eventResponseShortDto.setId(event.getId());
        eventResponseShortDto.setInitiator(
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventResponseShortDto.setPaid(event.getPaid());
        eventResponseShortDto.setTitle(event.getTitle());
        eventResponseShortDto.setViews(0);

        return eventResponseShortDto;
    }

    public static EventResponseLongDto toEventResponseLongDto(final Event event) {

        final EventResponseLongDto eventResponseLongDto = new EventResponseLongDto();

        eventResponseLongDto.setAnnotation(event.getAnnotation());
        eventResponseLongDto.setCategory(
                new CategoryResponseDto(event.getCategory().getId(), event.getCategory().getName()));
        eventResponseLongDto.setConfirmedRequests(event.getConfirmedRequests());
        eventResponseLongDto.setCreatedOn(event.getCreatedOn());
        eventResponseLongDto.setDescription(event.getDescription());
        eventResponseLongDto.setEventDate(event.getEventDate());
        eventResponseLongDto.setId(event.getId());
        eventResponseLongDto.setInitiator(
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventResponseLongDto.setLocation(new Location(event.getLat(), event.getLon()));
        eventResponseLongDto.setPaid(event.getPaid());
        eventResponseLongDto.setParticipantLimit(event.getParticipantLimit());
        eventResponseLongDto.setPublishedOn(event.getPublishedOn());
        eventResponseLongDto.setRequestModeration(event.getRequestModeration());
        eventResponseLongDto.setState(event.getState());
        eventResponseLongDto.setTitle(event.getTitle());
        eventResponseLongDto.setViews(0);

        return eventResponseLongDto;
    }
}
