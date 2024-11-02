package ru.practicum.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("UserRepository_empty")
    public void testEmpty() {

        final List<User> users = userRepository.findAll();

        assertTrue(users.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("UserRepository_findByIdIn")
    public void testFindByIdIn() {

        final User user1 = new User();
        user1.setName("Olia");
        user1.setEmail("molnia@yandex.ru");
        userRepository.save(user1);

        final User user2 = new User();
        user2.setName("Katia");
        user2.setEmail("gromgrommolnia@yandex.ru");
        userRepository.save(user2);

        final User user3 = new User();
        user3.setName("Tania");
        user3.setEmail("tania@yandex.ru");
        userRepository.save(user3);

        final Pageable pageable = PageRequest.of(0, 10);

        final List<Long> userIds = List.of(1L, 2L);

        final List<User> users = userRepository.findByIdIn(userIds, pageable);
        final List<User> usersAll = userRepository.findAll();

        assertEquals(2, users.size());
        assertEquals(3, usersAll.size());
    }


}