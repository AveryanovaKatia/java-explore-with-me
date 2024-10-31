package ru.practicum.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public List<UserResponseDto> findAll(final List<Long> userIds,
                                         final Pageable pageable) {
        log.info("Запрос на получение списка пользователей");
        final List<User> users;
        if (Objects.isNull(userIds) || userIds.isEmpty()) {
            users = userRepository.findAll(pageable).getContent();
            log.info("Получен списк всех пользователей");
        } else {
            users = userRepository.findByIdIn(userIds, pageable);
            log.info("Получен списк пользователей по заданным id");
        }
        return users.stream().map(UserMapper::toUserResponseDto).toList();
    }

    @Override
    public UserResponseDto save(final UserRequestDto userRequestDto) {
        log.info("Запрос на добавление пользователя ");
        try {
        final User user = userRepository.save(UserMapper.toUser(userRequestDto));
        log.info("Пользователь успешно добавлен под id {}", user.getId());
        return UserMapper.toUserResponseDto(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Адрес электронной почты уже занят почты");
        }
    }

    @Override
    public void delete(final Long userId) {
        log.info("Запрос на удаление пользователя с id {}", userId);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id = {} нет." + userId));
        userRepository.delete(user);
        log.info("Пользователь с id {} успешно удален ", userId);
    }
}
