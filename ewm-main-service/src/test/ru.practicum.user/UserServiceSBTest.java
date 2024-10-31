package ru.practicum.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceSBTest {

    UserService userService;

    final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    public void setUp() {

        final UserRequestDto userRequestDto1 = new UserRequestDto();
        userRequestDto1.setName("Katia");
        userRequestDto1.setEmail("gromgrommolnia@mail.ru");
        userService.save(userRequestDto1);

        final UserRequestDto userRequestDto2 = new UserRequestDto();
        userRequestDto2.setName("Nika");
        userRequestDto2.setEmail("moemore@mail.ru");
        userService.save(userRequestDto2);

        final UserRequestDto userRequestDto3 = new UserRequestDto();
        userRequestDto3.setName("Mia");
        userRequestDto3.setEmail("midnight@mail.ru");
        userService.save(userRequestDto3);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("UserService_findAll")
    void testFindAll() {

        assertEquals(1, userService.findAll(List.of(1L), pageable).size());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("UserService_create")
    void testCreate() {

        assertEquals(3, userService.findAll(List.of(1L, 2L, 3L), pageable).size());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("UserService_delete")
    void testDelete() {

        userService.delete(3L);

        assertEquals(2, userService.findAll(List.of(), pageable).size());

        userService.delete(2L);
        userService.delete(1L);

        assertEquals(0, userService.findAll(List.of(), pageable).size());
    }
}