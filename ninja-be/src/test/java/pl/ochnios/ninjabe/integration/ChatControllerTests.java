package pl.ochnios.ninjabe.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.ninjabe.TestUtils.asJsonString;
import static pl.ochnios.ninjabe.TestUtils.generateTooLongString;

import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.ninjabe.model.dtos.chat.ChatRequestDto;
import pl.ochnios.ninjabe.model.seeders.ConversationSeeder;
import pl.ochnios.ninjabe.model.seeders.UserSeeder;
import pl.ochnios.ninjabe.repositories.impl.ConversationCrudRepository;
import pl.ochnios.ninjabe.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
public class ChatControllerTests {

    private static final String CHAT_URL = "/chat";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private ConversationSeeder conversationSeeder;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Autowired
    private ConversationCrudRepository conversationCrudRepository;

    @BeforeAll
    public void setup() {
        userCrudRepository.deleteAll();
        userSeeder.seed();
    }

    @BeforeEach
    public void beforeEach() {
        conversationCrudRepository.deleteAll();
        conversationSeeder.seed();
    }

    @AfterAll
    public void tearDown() {
        conversationCrudRepository.deleteAll();
        userCrudRepository.deleteAll();
    }

    @Nested
    @DisplayName("Chat")
    @WithMockUser(username = "user")
    class Chat {

        @Test
        public void new_chat_200() throws Exception {
            final var chatRequestDto = new ChatRequestDto(null, "Answer with OK and nothing more");
            final var requestBuilder = MockMvcRequestBuilders.post(CHAT_URL)
                    .content(asJsonString(chatRequestDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.conversationId", is(not(emptyOrNullString()))))
                    .andExpect(jsonPath("$.completion", containsString("OK")));
        }

        @Test
        public void continue_chat_200() throws Exception {
            final var conversationId = UUID.nameUUIDFromBytes("c1".getBytes());
            final var chatRequestDto = new ChatRequestDto(conversationId, "What was my first message?");
            final var requestBuilder = MockMvcRequestBuilders.post(CHAT_URL)
                    .content(asJsonString(chatRequestDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.conversationId", is(conversationId.toString())))
                    .andExpect(jsonPath("$.completion", containsString("Hello!")));
        }

        @Test
        public void continue_not_existing_chat_404() throws Exception {
            final var conversationId = UUID.nameUUIDFromBytes("not existing".getBytes());
            final var chatRequestDto = new ChatRequestDto(conversationId, "Blah blah");
            final var requestBuilder = MockMvcRequestBuilders.post(CHAT_URL)
                    .content(asJsonString(chatRequestDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0]", containsString("Not found")));
        }

        @Test
        public void chat_null_question_400() throws Exception {
            final var chatRequestDto = new ChatRequestDto(null, null);
            test_validation(chatRequestDto, "question must not be blank or null");
        }

        @Test
        public void chat_blank_question_400() throws Exception {
            final var chatRequestDto = new ChatRequestDto(null, "     ");
            test_validation(chatRequestDto, "question must not be blank or null");
        }

        @Test
        public void chat_too_short_question_400() throws Exception {
            final var chatRequestDto = new ChatRequestDto(null, "ok");
            test_validation(chatRequestDto, "question must have at least");
        }

        @Test
        public void chat_too_long_question_400() throws Exception {
            final var question = generateTooLongString(8193);
            final var chatRequestDto = new ChatRequestDto(null, question);
            test_validation(chatRequestDto, "question must have at most");
        }

        private void test_validation(ChatRequestDto chatRequestDto, String expectedError) throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.post(CHAT_URL)
                    .content(asJsonString(chatRequestDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString(expectedError)));
        }
    }
}
