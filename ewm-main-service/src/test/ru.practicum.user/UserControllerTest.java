package ru.practicum.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.user.dto.UserRequestDto;
import ru.practicum.user.dto.UserResponseDto;
import ru.practicum.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("UserController_save")
    void testSave() throws Exception {

        final UserResponseDto userResponseDto= new UserResponseDto();

        when(userService.save(any(UserRequestDto.class))).thenReturn(userResponseDto);

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Lena\", \"email\": \"gromgrommolnia@mail.ru\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponseDto.getId()));

        verify(userService, times(1)).save(any(UserRequestDto.class));
    }


    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("UserController_findAll")
    void testFindAll() throws Exception {

        final UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);

        final List<UserResponseDto> users = List.of(userResponseDto);

        when(userService.findAll(eq(List.of(1L)), any(Pageable.class))).thenReturn(users);

        mockMvc.perform(get("/admin/users")
                        .param("userIds", "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())  // Ожидаем статус 200
                .andExpect(jsonPath("$[0].id").value(userResponseDto.getId()));

        verify(userService, times(1)).findAll(eq(List.of(1L)), any(Pageable.class));
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("UserController_delete")
    void testDelete() throws Exception {

        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }
}
