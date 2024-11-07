package ru.practicum.comment;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.category.contoller.AdminCategoryController;
import ru.practicum.category.dto.CategoryRequestDto;
import ru.practicum.comment.controller.AdminCommentController;
import ru.practicum.comment.controller.PrivateCommentController;
import ru.practicum.comment.controller.PublicCommentController;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.event.contoller.AdminEventController;
import ru.practicum.event.contoller.PrivateEventController;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventUpdateDto;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.StateAction;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.contoller.UserController;
import ru.practicum.user.dto.UserRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentControllerSBTest {

    PrivateCommentController privateCommentController;

    PublicCommentController publicCommentController;

    AdminCommentController adminCommentController;

    UserController userController;

    PrivateEventController privateEventController;

    AdminEventController adminEventController;

    AdminCategoryController adminCategoryController;

    @BeforeEach
    public void setUp() {

        final UserRequestDto userRequestDto1 = new UserRequestDto();
        userRequestDto1.setName("Katia");
        userRequestDto1.setEmail("gromgrommolnia@mail.ru");
        userController.save(userRequestDto1);

        final UserRequestDto userRequestDto2 = new UserRequestDto();
        userRequestDto2.setName("Nika");
        userRequestDto2.setEmail("moemore@mail.ru");
        userController.save(userRequestDto2);

        final UserRequestDto userRequestDto3 = new UserRequestDto();
        userRequestDto3.setName("Mia");
        userRequestDto3.setEmail("midnight@mail.ru");
        userController.save(userRequestDto3);

        final CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
        categoryRequestDto.setName("Хэллоуин");
        adminCategoryController.save(categoryRequestDto);

        final EventRequestDto eventRequestDto1 = new EventRequestDto();
        eventRequestDto1.setAnnotation("Будет страшно весело");
        eventRequestDto1.setEventDate(LocalDateTime.now().plusDays(20));
        eventRequestDto1.setLocation(new Location(20.20, 30.30));
        eventRequestDto1.setCategory(1L);
        eventRequestDto1.setDescription("Дом с призраками — жилой дом или иное здание, " +
                "принимаемое за населённое бестелесными духами умерших, " +
                "которыми могут быть бывшие его жители или фамильяры.");
        eventRequestDto1.setTitle("Дом с призраками");
        privateEventController.save(1L, eventRequestDto1);

        final EventRequestDto eventRequestDto2 = new EventRequestDto();
        eventRequestDto2.setAnnotation("Будет страшно и не весело");
        eventRequestDto2.setEventDate(LocalDateTime.now().plusDays(20));
        eventRequestDto2.setLocation(new Location(20.20, 40.40));
        eventRequestDto2.setCategory(1L);
        eventRequestDto2.setDescription("Однажды Кэролайн обнаруживает на чердаке секретную комнату с массой" +
                " мистических предметов. Хозяйка утверждает, что вещи принадлежат бывшим владельцам, " +
                "которые занимались чёрной магией. Вскоре Кэролайн становится свидетельницей довольно странных" +
                " и необъяснимых событий и решает во что бы то ни стало разгадать секрет таинственной комнаты.");
        eventRequestDto2.setTitle("Ключ от всех дверей");
        privateEventController.save(1L, eventRequestDto2);

        final EventRequestDto eventRequestDto3 = new EventRequestDto();
        eventRequestDto3.setAnnotation("Будет Виктор и его невеста");
        eventRequestDto3.setEventDate(LocalDateTime.now().plusDays(21));
        eventRequestDto3.setLocation(new Location(10.10, 10.10));
        eventRequestDto3.setCategory(1L);
        eventRequestDto3.setDescription("Виктор приходит в себя в потустороннем мире. " +
                "Как ни странно, этот мир гораздо веселее, красочнее и жизнерадостнее, чем мир живых." +
                " Там Виктор узнаёт историю Эмили. Некогда приезжий обаятельный аристократ " +
                "соблазнил её и уговорил бежать с ним, но потом убил и забрал семейные драгоценности. " +
                "Очнувшись и поняв, что она мертва, Эмили поклялась, что будет ждать настоящей любви," +
                " — и вот появился Виктор, произнесший слова свадебной клятвы и надевший ей кольцо на палец.");
        eventRequestDto3.setTitle("Труп невесты");
        privateEventController.save(3L, eventRequestDto3);

        final EventUpdateDto eventUpdateDto = new EventUpdateDto();
        eventUpdateDto.setStateAction(StateAction.PUBLISH_EVENT);

        adminEventController.approveEventByAdmin(1L, eventUpdateDto);
        adminEventController.approveEventByAdmin(2L, eventUpdateDto);
        adminEventController.approveEventByAdmin(3L, eventUpdateDto);



        final CommentRequestDto commentRequestDto1 = new CommentRequestDto();
        commentRequestDto1.setText("Интересно на это посмотреть");
        privateCommentController.save(commentRequestDto1, 2L, 1L);

        final CommentRequestDto commentRequestDto2 = new CommentRequestDto();
        commentRequestDto2.setText("Точно пойду");
        privateCommentController.save(commentRequestDto2, 2L, 1L);

        final CommentRequestDto commentRequestDto3 = new CommentRequestDto();
        commentRequestDto3.setText("Не смогу, так как буду в это время в Доме с призраками");
        privateCommentController.save(commentRequestDto3, 2L, 2L);

        final CommentRequestDto commentRequestDto4 = new CommentRequestDto();
        commentRequestDto4.setText("Надеюсь стоит того, посмотрю");
        privateCommentController.save(commentRequestDto4, 2L, 3L);


        final CommentRequestDto commentRequestDto5 = new CommentRequestDto();
        commentRequestDto5.setText("А я уже бывала на таком");
        privateCommentController.save(commentRequestDto5, 3L, 1L);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("PublicCategoryController_findByIdOk")
    void testPublicCategoryControllerFindByIdOk() {

        final ResponseEntity<CommentResponseDto> commentResponseDto =
                publicCommentController.findById(1L);

        assertEquals(HttpStatus.OK, commentResponseDto.getStatusCode());
        assertEquals("Интересно на это посмотреть",
                Objects.requireNonNull(commentResponseDto.getBody()).getText());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("PublicCategoryController_findByIdBadRequest")
    void testPublicCategoryControllerFindByIdBadRequest() {

        assertThrows(
                NotFoundException.class,
                () -> publicCommentController.findById(6L)
        );
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("PublicCategoryController_findByEvent")
    void testPublicCategoryControllerFindByEvent() {

     final ResponseEntity<List<CommentResponseDto>> commentResponseDtos =
             publicCommentController.findByEvent(1L, 0, 10);

        assertEquals(HttpStatus.OK, commentResponseDtos.getStatusCode());
        assertEquals(3, Objects.requireNonNull(commentResponseDtos.getBody()).size());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("PrivateCategoryController_findAll")
    void testPrivateCategoryControllerFindAll() {

        final ResponseEntity<List<CommentResponseDto>> commentResponseDtos =
                privateCommentController.findAll(2L, 3L, 0, 10);

        assertEquals(HttpStatus.OK, commentResponseDtos.getStatusCode());
        assertEquals(1, Objects.requireNonNull(commentResponseDtos.getBody()).size());
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("PrivateCategoryController_update")
    void testPrivateCategoryControllerUpdate() {

        final CommentRequestDto commentRequestDto6 = new CommentRequestDto();
        commentRequestDto6.setText("ха-ха");

        final ResponseEntity<CommentResponseDto> commentResponseDto =
                privateCommentController.update(commentRequestDto6, 2L, 1L);

        assertEquals(HttpStatus.OK, commentResponseDto.getStatusCode());
        assertEquals("ха-ха", Objects.requireNonNull(commentResponseDto.getBody()).getText());
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("PrivateCategoryController_delete")
    void testPrivateCategoryControllerDelete() {


        final ResponseEntity<String> response =
                privateCommentController.delete( 2L, 1L);

        final ResponseEntity<List<CommentResponseDto>> commentResponseDtos =
                privateCommentController.findAll(2L, 1L, 0, 10);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(commentResponseDtos.getBody()).size());
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("AdminCategoryController_deleteByIds")
    void testAdminCategoryControllerDeleteByIds() {

        final ResponseEntity<String> response =
                adminCommentController.deleteByIds(List.of(1L, 2L));

        final ResponseEntity<List<CommentResponseDto>> commentResponseDtos =
                publicCommentController.findByEvent(1L, 0, 10);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(commentResponseDtos.getBody()).size());
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("AdminCategoryController_deleteByEventId")
    void testAdminCategoryControllerDeleteByEventId() {

        final ResponseEntity<String> response =
                adminCommentController.deleteByEventId(1L);

        final ResponseEntity<List<CommentResponseDto>> commentResponseDtos =
                publicCommentController.findByEvent(1L, 0, 10);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(0, Objects.requireNonNull(commentResponseDtos.getBody()).size());
    }
}
