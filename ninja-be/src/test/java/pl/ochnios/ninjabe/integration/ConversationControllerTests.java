package pl.ochnios.ninjabe.integration;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.ninjabe.TestUtils.asJsonString;
import static pl.ochnios.ninjabe.TestUtils.asParamsMap;
import static pl.ochnios.ninjabe.TestUtils.generateTooLongString;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.ninjabe.commons.AppConstants;
import pl.ochnios.ninjabe.commons.patch.JsonPatchDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.model.entities.conversation.Conversation;
import pl.ochnios.ninjabe.model.seeders.ConversationSeeder;
import pl.ochnios.ninjabe.model.seeders.UserSeeder;
import pl.ochnios.ninjabe.repositories.impl.ConversationCrudRepository;
import pl.ochnios.ninjabe.repositories.impl.MessageCrudRepository;
import pl.ochnios.ninjabe.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ConversationControllerTests {

    private static final String CONVERSATIONS_URI = "/conversations";

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

    @Autowired
    private MessageCrudRepository messageCrudRepository;

    private final UUID conversationId = UUID.nameUUIDFromBytes("c1".getBytes());

    @BeforeAll
    public void setup() {
        userCrudRepository.deleteAll();
        userSeeder.seed();
    }

    @BeforeEach
    public void beforeEach() {
        conversationCrudRepository.deleteAll();
        messageCrudRepository.deleteAll();
        conversationSeeder.seed();
    }

    @Nested
    @DisplayName("Get")
    @WithMockUser(username = "user")
    class Get {

        private final UUID conversationId = UUID.nameUUIDFromBytes("c1".getBytes());
        private final String conversationSummary = "New conversation";

        @Test
        public void get_summaries_200() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI + "/summaries");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(conversationId.toString())))
                    .andExpect(jsonPath("$.items[0].summary", is(conversationSummary)))
                    .andExpect(jsonPath("$.items[0].createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void get_summaries_2nd_page_200() throws Exception {
            final var user = userCrudRepository.findByUsername("user");
            final var conversation = Conversation.builder()
                    .id(UUID.nameUUIDFromBytes("c2".getBytes()))
                    .summary("Second conversation")
                    .user(user.orElseThrow(() -> new RuntimeException("User not found")))
                    .build();
            conversationCrudRepository.save(conversation);

            final var pageRequest = new PageRequestDto(1, 1, "summary", "asc");
            final var requestBuilder =
                    MockMvcRequestBuilders.get(CONVERSATIONS_URI + "/summaries").params(asParamsMap(pageRequest));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(1)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(2)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(conversation.getId().toString())))
                    .andExpect(jsonPath("$.items[0].summary", is(conversation.getSummary())))
                    .andExpect(jsonPath("$.items[0].createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void get_summaries_2nd_page_empty_200() throws Exception {
            final var pageRequest = new PageRequestDto(1, 1, null, null);
            final var requestBuilder =
                    MockMvcRequestBuilders.get(CONVERSATIONS_URI + "/summaries").params(asParamsMap(pageRequest));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(1)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void get_by_id_200() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI + "/" + conversationId);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(conversationId.toString())))
                    .andExpect(jsonPath("$.summary", is(conversationSummary)))
                    .andExpect(jsonPath("$.createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void get_by_id_404() throws Exception {
            final var notExistingId = UUID.nameUUIDFromBytes("not existing".getBytes());
            final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI + "/" + notExistingId);
            mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Patch")
    @WithMockUser(username = "user")
    class Patch {

        @Test
        public void patch_conversation_200() throws Exception {
            final var patch = new JsonPatchDto("replace", "/summary", "New summary");
            final var requestBuilder = MockMvcRequestBuilders.patch(CONVERSATIONS_URI + "/" + conversationId)
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(conversationId.toString())))
                    .andExpect(jsonPath("$.summary", is(patch.getValue())))
                    .andExpect(jsonPath("$.createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void patch_conversation_summary_blank_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/summary", "   ");
            test_validation(patch, "summary must not be blank or null");
        }

        @Test
        public void patch_conversation_summary_null_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/summary", null);
            test_validation(patch, "summary must not be blank or null");
        }

        @Test
        public void patch_conversation_summary_too_short_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/summary", "xx");
            test_validation(patch, "summary must have at least");
        }

        @Test
        public void patch_conversation_summary_too_long_400() throws Exception {
            final var tooLongSummary = generateTooLongString(33);
            final var patch = new JsonPatchDto("replace", "/summary", tooLongSummary);
            test_validation(patch, "summary must have at most");
        }

        @Test
        public void patch_conversation_not_patchable_field_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/id", UUID.nameUUIDFromBytes("fake".getBytes()));
            test_validation(patch, "'id' is not patchable");
        }

        @Test
        public void patch_conversation_not_existing_field_400() throws Exception {
            final var patch = new JsonPatchDto("add", "/owner", "johndoe");
            test_validation(patch, "'owner' does not exist in");
        }

        private void test_validation(JsonPatchDto jsonPatchDto, String expectedError) throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.patch(CONVERSATIONS_URI + "/" + conversationId)
                    .content(asJsonString(Set.of(jsonPatchDto)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString(expectedError)));
        }
    }

    @Nested
    @DisplayName("Delete")
    @WithMockUser(username = "user")
    class Delete {

        @Test
        public void delete_conversation_204() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.delete(CONVERSATIONS_URI + "/" + conversationId);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        public void delete_conversation_404() throws Exception {
            final var notExistingId = UUID.nameUUIDFromBytes("not existing".getBytes());
            final var requestBuilder = MockMvcRequestBuilders.delete(CONVERSATIONS_URI + "/" + notExistingId);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0]", containsString("Not found")));
        }
    }

    @Nested
    @DisplayName("Search forbidden")
    @WithMockUser(username = "user")
    class SearchForbidden {
        // TODO write search tests
    }

    @Nested
    @DisplayName("Search")
    @WithMockUser(username = "mod")
    class Search {
        // TODO write search tests
    }
}
