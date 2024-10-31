package ru.practicum.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.QCategory;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseLongDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.dto.EventUpdateUserDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.stat.service.StatsService;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EventServiceImpl implements EventService {

    EventRepository eventRepository;

    UserRepository userRepository;

    CategoryRepository categoryRepository;

    RequestRepository requestRepository;

    StatsService statsService;

    EntityManager entityManager;

    //////////------------Private------------//////////

    @Transactional(readOnly = true)
    @Override
    public List<EventResponseShortDto> findAll(final Long userId,
                                               final Pageable pageable) {
        log.info("Запрос на получение событий пользователя с id = {}", userId);
        final List<Event> events = eventRepository.findByInitiatorId(userId, pageable);
        if (events.isEmpty()) {
            log.info("У пользователя с id {} еще нет событий", userId);
            return new ArrayList<>();
        }
        log.info("Получен список событий пользователя с id {}", userId);
        return events.stream().map(EventMapper::toEventResponseShortDto).toList();
    }

    @Override
    public EventResponseLongDto save(final Long userId,
                                     final EventRequestDto eventRequestDto) {
        log.info("Запрос на добавление нового события");
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Category category = categoryRepository.findById(eventRequestDto.getCategory())
                .orElseThrow(() -> new ValidationException("Категория указана неверно"));
        if (eventRequestDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("Событие не может начинаться ранее чем за 2 часа до создания");
        }
        final Event event = eventRepository
                .save(EventMapper.toEvent(eventRequestDto, user, category));
        log.info("Событие успешно добавлено под id {} и со статусом {} и дожтдается подтверждения",
                user.getId(), event.getState());
        return EventMapper.toEventResponseLongDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public EventResponseLongDto findById(final Long userId,
                                         final Long eventId) {
        log.info("Запрос на получение события с id = {}", eventId);
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("Только пользователь создавший событие может получить его полное описание");
        }
        log.info("получено событие с id = {}", eventId);
        return EventMapper.toEventResponseLongDto(event);
    }

    @Override
    public EventResponseLongDto update(final Long userId,
                                       final Long eventId,
                                       final EventUpdateUserDto eventUpdateUserDto) {
        log.info("Запрос на обновление события с id = {}", eventId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        if (!oldEvent.getInitiator().getId().equals(user.getId())) {
            throw new ValidationException("Только пользователь создавший событие может его редактировать");
        }
        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Нельзя изменить опубликованное событие, или переданный статут несуществует");
        }
        if (Objects.nonNull(eventUpdateUserDto.getEventDate()) &&
                eventUpdateUserDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Событие не может начинаться ранее чем через 2 часа после обновления");
        }

        if (Objects.nonNull(eventUpdateUserDto.getAnnotation())) {
            oldEvent.setAnnotation(eventUpdateUserDto.getAnnotation());
        }
        if (Objects.nonNull(eventUpdateUserDto.getCategory())) {
            final Category category = categoryRepository.findById(eventUpdateUserDto.getCategory())
                    .orElseThrow(() -> new ValidationException("Категория указана неверно"));
            oldEvent.setCategory(category);
        }
        if (Objects.nonNull(eventUpdateUserDto.getDescription())) {
            oldEvent.setDescription(eventUpdateUserDto.getDescription());
        }
        if (Objects.nonNull(eventUpdateUserDto.getEventDate())) {
            oldEvent.setEventDate(eventUpdateUserDto.getEventDate());
        }
        if (Objects.nonNull(eventUpdateUserDto.getLocation())) {
            oldEvent.setLon(eventUpdateUserDto.getLocation().lon());
            oldEvent.setLat(eventUpdateUserDto.getLocation().lat());
        }
        if (Objects.nonNull(eventUpdateUserDto.getPaid())) {
            oldEvent.setPaid(eventUpdateUserDto.getPaid());
        }
        if (Objects.nonNull(eventUpdateUserDto.getParticipantLimit())) {
            oldEvent.setParticipantLimit(eventUpdateUserDto.getParticipantLimit());
        }
        if (Objects.nonNull(eventUpdateUserDto.getRequestModeration())) {
            oldEvent.setRequestModeration(eventUpdateUserDto.getRequestModeration());
        }
        if (Objects.nonNull(eventUpdateUserDto.getStateAction()) &&
                eventUpdateUserDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
               oldEvent.setState(EventState.PENDING);
        }
        if (Objects.nonNull(eventUpdateUserDto.getStateAction()) &&
                eventUpdateUserDto.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
            oldEvent.setState(EventState.CANCELED);
        }
        if (Objects.nonNull(eventUpdateUserDto.getTitle())) {
            oldEvent.setTitle(eventUpdateUserDto.getTitle());
        }
        final Event event = eventRepository.save(oldEvent);
        log.info("Событие успешно обновлено под id {} и дожидается подтверждения", eventId);
        return EventMapper.toEventResponseLongDto(event);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> findRequestsByEventId(final Long userId,
                                                  final Long eventId) {
        log.info("Запрос на получение всех заявок на событие с id = {}", eventId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("У этого события другой инициатор");
        }
        final List<Request> requests = requestRepository.findByEventId(eventId);
        if (requests.isEmpty()) {
            log.info("Пока нет заявок на участие в мероприятии с id = {}", eventId);
            return new ArrayList<>();
        }
        log.info("Получен список всех заявок на участие в мероприятии с id {}", eventId);
        return requests.stream().map(RequestMapper::toRequestDto).toList();
    }

    @Override
    public Map<String, List<RequestDto>> approveRequests(final Long userId,
                                                         final Long eventId,
                                                         final RequestUpdateDto requestUpdateDto) {
        log.info("Запрос на изменение статуса переданных заявок");
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        if (!event.getInitiator().equals(user)) {
            throw new ConflictException("У этого события другой инициатор");
        }
        final List<Request> requests = requestRepository.findRequestByIdIn(requestUpdateDto.getRequestIds());
        if (event.getRequestModeration() &&
                event.getParticipantLimit().equals(event.getConfirmedRequests()) &&
                event.getParticipantLimit() != 0 &&
                requestUpdateDto.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictException("Лимит заявок на участие в событии исчерпан");
        }
        final boolean verified = requests.stream()
                .allMatch(request -> request.getEvent().getId().longValue() == eventId);
        if (!verified) {
            throw new ConflictException("Неверно передан список запросов. Они должны относиться к одномоу событию.");
        }
        final Map<String, List<RequestDto>> requestMap = new HashMap<>();
        if (requestUpdateDto.getStatus().equals(RequestStatus.REJECTED)) {
            log.info("Запрос на установление статуса заявкам <ОТМЕНЕНА>");
            if (requests.stream()
                    .anyMatch(request -> request.getStatus().equals(RequestStatus.CONFIRMED))) {
                throw new ConflictException("Нельзя отменить уже подтвержденные заявки.");
            }
            log.info("Отклонены все заявки");
            List<RequestDto> rejectedRequests = requests.stream()
                    .peek(request -> request.setStatus(RequestStatus.REJECTED))
                    .map(requestRepository::save)
                    .map(RequestMapper::toRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);
        } else {
                log.info("Запрос на установление статуса заявкам <ПОДТВЕРЖДЕНА");
            if (requests.stream()
                    .anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING))) {
                throw new ConflictException("Чтобы поставить статус <ПОДТВЕРЖДЕН>, " +
                        "все заявки должны быть со статусом <В ОЖИДАНИИ>");
            }
                long limit = event.getParticipantLimit() - event.getConfirmedRequests();
                final List<Request> confirmedList = requests.stream()
                        .limit(limit)
                        .peek(request -> request.setStatus(RequestStatus.CONFIRMED))
                        .map(requestRepository::save).toList();
                log.info("Заявки на участие сохранены со статусом <ПОДТВЕРЖДЕНА>");
            final List<RequestDto> confirmedRequests =  confirmedList.stream()
                    .map(RequestMapper::toRequestDto)
                    .toList();
            requestMap.put("confirmedRequests", confirmedRequests);

            final List<Request> rejectedList = requests.stream()
                        .skip(limit)
                        .peek(request -> request.setStatus(RequestStatus.REJECTED))
                        .map(requestRepository::save).toList();
                log.info("Часть заявок сохранено со статусом <ОТМЕНЕНА>, так как превышен лимит");
            final List<RequestDto> rejectedRequests =  rejectedList.stream()
                    .map(RequestMapper::toRequestDto)
                    .toList();
            requestMap.put("rejectedRequests", rejectedRequests);

                event.setConfirmedRequests(confirmedList.size() + event.getConfirmedRequests());
                eventRepository.save(event);
        }
        return requestMap;
    }

    //////////------------Admin------------//////////

    @Transactional(readOnly = true)
    @Override
    public List<EventResponseLongDto> findOnParameters(final List<Long> usersId,
                                                       final List<String> states,
                                                       final List<Long> categoriesId,
                                                       final LocalDateTime start,
                                                       final LocalDateTime end,
                                                       final Pageable pageable) {
        log.info("Заявка на получение событий по переданным параметрам");
        if (start.isAfter(end)) {
            throw new ValidationException("Временной промежуток задан неверно");
        }
        List<User> users;
        if (Objects.isNull(usersId) || usersId.isEmpty()) {
            users = userRepository.findAll();
            if (users.isEmpty()) {
                log.info("Еще нет ни одного пользователя, а значит и событий нет");
                return new ArrayList<>();
            }
        } else {
            users = userRepository.findByIdIn(usersId, pageable);
            if (users.size() != usersId.size()) {
                throw new ValidationException("Список пользователей передан неверно");
            }
        }
        final List<EventState> eventStates;
        if (Objects.isNull(states) || states.isEmpty()) {
            eventStates = List.of(EventState.PUBLISHED, EventState.CANCELED, EventState.PENDING);
        } else {
            try {
                eventStates = states.stream()
                        .map(EventState::valueOf)
                        .toList();
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Недопустимое значение статуса: " + e.getMessage());
            }
        }
        List<Category> categories;
        if (categoriesId == null) {
            categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                log.info("Еще нет ни одной категории, а значит и событий нет");
                return new ArrayList<>();
            }
        } else {
            categories = categoryRepository.findByIdIn(categoriesId, pageable);
            if (categories.size() != categoriesId.size()) {
                throw new ValidationException("Список категорий передан неверно неверно");
            }
        }
        final List<Event> events = eventRepository
                    .findByInitiatorInAndStateInAndCategoryInAndEventDateAfterAndEventDateBefore(
                            users, eventStates, categories, start, end, pageable);
        if (events.isEmpty()) {
            log.info("По данным параметрам не нашлось ни одного события");
            return new ArrayList<>();
        }
        log.info("Получен список событий по заданным параметрам");
        return events.stream().map(EventMapper::toEventResponseLongDto).toList();
    }


    @Override
    public EventResponseLongDto approveEventByAdmin(final Long eventId,
                                                    final EventUpdateDto eventUpdateDto) {
        log.info("Заявка к администратору на публикацию события с id = {}", eventId);
        final Event oldEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));

        if ((Objects.nonNull(eventUpdateDto.getEventDate()) &&
        eventUpdateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) ||
        oldEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ConflictException("Событие не может начинаться ранее" +
                    " чем через 1 час после редактирования администратором");
        }
        if (oldEvent.getPublishedOn() != null && LocalDateTime.now().plusHours(1).isBefore(oldEvent.getPublishedOn())) {
            throw new ConflictException("Дата начала изменяемого события должна быть не ранее " +
                    "чем за час от даты публикации.");
        }
        if (oldEvent.getState().equals(EventState.PUBLISHED) ||
                oldEvent.getState().equals(EventState.CANCELED)) {
            throw new ConflictException("Администратор не может менять статус опубликованного или отмененного события");
        }
        if (Objects.nonNull(eventUpdateDto.getAnnotation())) {
            oldEvent.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (Objects.nonNull(eventUpdateDto.getCategory())) {
            final Category category = categoryRepository.findById(eventUpdateDto.getCategory())
                    .orElseThrow(() -> new ValidationException("Категория указана неверно"));
            oldEvent.setCategory(category);
        }
        if (Objects.nonNull(eventUpdateDto.getDescription())) {
            oldEvent.setDescription(eventUpdateDto.getDescription());
        }
        if (Objects.nonNull(eventUpdateDto.getEventDate())) {
            oldEvent.setEventDate(eventUpdateDto.getEventDate());
        }
        if (Objects.nonNull(eventUpdateDto.getLocation())) {
            oldEvent.setLon(eventUpdateDto.getLocation().lon());
            oldEvent.setLat(eventUpdateDto.getLocation().lat());
        }
        if (Objects.nonNull(eventUpdateDto.getPaid())) {
            oldEvent.setPaid(eventUpdateDto.getPaid());
        }
        if (Objects.nonNull(eventUpdateDto.getParticipantLimit())) {
            oldEvent.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (Objects.nonNull(eventUpdateDto.getRequestModeration())) {
            oldEvent.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (Objects.nonNull(eventUpdateDto.getStateAction()) &&
                oldEvent.getState().equals(EventState.PENDING) &&
                eventUpdateDto.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            oldEvent.setState(EventState.PUBLISHED);
            oldEvent.setPublishedOn(LocalDateTime.now());
        }
        if (Objects.nonNull(eventUpdateDto.getStateAction()) &&
                oldEvent.getState().equals(EventState.PENDING) &&
                eventUpdateDto.getStateAction().equals(StateAction.REJECT_EVENT)) {
            oldEvent.setState(EventState.CANCELED);
            oldEvent.setPublishedOn(null);
        }
        if (Objects.nonNull(eventUpdateDto.getTitle())) {
            oldEvent.setTitle(eventUpdateDto.getTitle());
        }
        final Event event = eventRepository.save(oldEvent);
        log.info("Событие успешно обновлено администратором");
        return EventMapper.toEventResponseLongDto(event);
    }

    //////////------------Public------------//////////

    @Transactional(readOnly = true)
    @Override
    public List<EventResponseShortDto> findAllPublic(final String text,
                                                     final List<Long> categories,
                                                     final Boolean paid,
                                                     final LocalDateTime start,
                                                     final LocalDateTime end,
                                                     final boolean onlyAvailable,
                                                     final EventSort sort,
                                                     final PageRequest pageRequest,
                                                     final HttpServletRequest request,
                                                     final int from) {
        log.info("Запрос на получение опубликованных событий");
        if (start.isAfter(end)) {
            throw new ValidationException("Временной промежуток задан неверно");
        }
        final QEvent event = QEvent.event;
        final QCategory category = QCategory.category;
        final JPAQuery<Event> query = new JPAQuery<>(entityManager);
        query.from(event).join(event.category, category).fetchJoin();
        BooleanExpression predicate = event.eventDate.goe(start);

        if (Objects.nonNull(text) && !text.isEmpty()) {
            predicate = predicate.and(event.annotation.toLowerCase().like("%" + text.toLowerCase() + "%")
                    .or(event.description.toLowerCase().like("%" + text.toLowerCase() + "%")));
        }
            predicate = predicate.and(event.eventDate.loe(end));
        if (Objects.nonNull(categories) && !categories.isEmpty()) {
            predicate = predicate.and(event.category.id.in(categories));
        }
        if (Objects.nonNull(paid)) {
            predicate = predicate.and(event.paid.eq(paid));
        }
            predicate = predicate.and(event.participantLimit.gt(event.confirmedRequests));

        query.where(predicate);
        query.offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize());
        final List<Event> events = query.fetch();
        final Map<Long, Long> eventAndViews = statsService
                .getView(events.stream().map(Event::getId).toList(), false);
        events.forEach(e -> e.setViews(Math.toIntExact(eventAndViews.getOrDefault(e.getId(), 0L))));

        if (Objects.nonNull(sort) && sort.equals(EventSort.EVENT_DATE)) {
            events.sort(Comparator.comparing(Event::getEventDate));
        }
        if (Objects.nonNull(sort) && sort.equals(EventSort.VIEWS)) {
            events.sort(Comparator.comparing(Event::getViews).reversed());
        }
        if (events.stream().noneMatch(event1 -> event1.getState().equals(EventState.PUBLISHED))) {
            throw new ValidationException("Нет опубликованных событий");
        }
        final Map<Long, Long> view = statsService.getView(events.stream().map(Event::getId).toList(), false);

        final List<Event> paginatedEvents = events.stream().skip(from).toList();

        statsService.createStats(request.getRequestURI(), request.getRemoteAddr());
        return paginatedEvents.stream().map(EventMapper::toEventResponseShortDto)
                .peek(dto -> {
                    Long viewCount = view.get(dto.getId());
                    dto.setViews(viewCount != null ? viewCount.intValue() : 0);
                }).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public EventResponseLongDto findByIdPublic(final Long eventId,
                                              final HttpServletRequest request) {
        log.info("Запрос на получение опубликованого события с id {}", eventId);
        final Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("События с id = {} нет." + eventId));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("У события должен быть статус <ОПУБЛИКОВАННО>");
        }
        final Map<Long, Long> view = statsService.getView(new ArrayList<>(List.of(event.getId())), true);
        final EventResponseLongDto eventResponseLongDto = EventMapper.toEventResponseLongDto(event);
        eventResponseLongDto.setViews(Math.toIntExact(view.getOrDefault(event.getId(), 0L)));

        statsService.createStats(request.getRequestURI(), request.getRemoteAddr());
        return eventResponseLongDto;
    }
}
