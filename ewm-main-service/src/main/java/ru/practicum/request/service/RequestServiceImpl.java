package ru.practicum.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {

    RequestRepository requestRepository;

    UserRepository userRepository;

    EventRepository eventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> findAll(final Long userId) {
        log.info("Запрос на получение всех заявок участия пользователя с id {}", userId);
        final List<Request> requests = requestRepository.findByRequesterId(userId);
        if (requests.isEmpty()) {
            log.info("У пользователя с id {} пока нет заявок на участии в мероприятии", userId);
            return new ArrayList<>();
        }
        log.info("Получен список всех заявок участия пользователя с id {}", userId);
        return requests.stream().map(RequestMapper::toRequestDto).toList();
    }

    @Override
    public RequestDto save(final Long userId,
                           final Long eventId) {
        log.info("Запрос на создание зявки на участие пользователя с id {} в событии с id {}", userId, eventId);
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Request requestValid = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (Objects.nonNull(requestValid)) {
            throw new ConflictException("Пользователь уже подал заявку на участи в событии");
        }
        if (event.getInitiator().equals(user)) {
            throw new ConflictException("Пользователь не может подать заяку на участие в своем же мероприятии");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя подавать заявку на неопубликованное мероприятие");
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                event.getParticipantLimit() != 0) {
            throw new ConflictException("На данное мероприятие больше нет мест");
        }
        final Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        if (event.getParticipantLimit() == 0 ||
                (!event.getRequestModeration() && event.getParticipantLimit() > event.getConfirmedRequests())) {
            request.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
            final Request newRequest = requestRepository.save(request);
            log.info("Заявка на участие сохранена со статусом <ПОДТВЕРЖДЕНА>");
            return RequestMapper.toRequestDto(newRequest);
        }
        if (!event.getRequestModeration() && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            request.setStatus(RequestStatus.REJECTED);
            final Request newRequest = requestRepository.save(request);
            log.info("Заявка на участие сохранена со статусом <ОТМЕНЕНА>, так как превышен лимит");
            return RequestMapper.toRequestDto(newRequest);
        }
        request.setStatus(RequestStatus.PENDING);
        final Request newRequest = requestRepository.save(request);
        log.info("Заявка на участие сохранена со статусом <В ОЖИДАНИИ>");
        return RequestMapper.toRequestDto(newRequest);
    }

    @Override
    public RequestDto cancelRequest(final Long userId,
                                    final Long requestId) {
        log.info("Запрос на отмену заявки на участие с id = {}", requestId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с id = {} не была подана." + requestId));
        if (!request.getRequester().equals(user)) {
            throw new ConflictException("Только пользователь подавший заявку может отменить ее");
        }
        request.setStatus(RequestStatus.CANCELED);
        final Request requestCancel = requestRepository.save(request);
        log.info("Заявка на участие с id = {} отменена", requestId);
        final Event event = request.getEvent();
        if (event.getRequestModeration()) {
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
            eventRepository.save(event);
            log.info("У события с id = {} появилось еще одно свободное место", event.getId());
        }
        return RequestMapper.toRequestDto(requestCancel);
    }
}
