package pl.ochnios.samurai.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.commons.patch.JsonPatchDto;
import pl.ochnios.samurai.model.dtos.conversation.ConversationCriteria;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.entities.conversation.Conversation;
import pl.ochnios.samurai.model.entities.conversation.MessageEntity;
import pl.ochnios.samurai.model.seeders.ConversationSeeder;
import pl.ochnios.samurai.model.seeders.UserSeeder;
import pl.ochnios.samurai.repositories.impl.ConversationCrudRepository;
import pl.ochnios.samurai.repositories.impl.UserCrudRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.samurai.TestUtils.asJsonString;
import static pl.ochnios.samurai.TestUtils.asParamsMap;
import static pl.ochnios.samurai.TestUtils.generateTooLongString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({ "local", "test" })
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

        private final UUID conversationId = UUID.nameUUIDFromBytes("c1".getBytes());
        private final String conversationSummary = "New conversation";

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
        @DisplayName("Get")
        @WithMockUser(username = "user")
        class Get {

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

                        final var pageRequest = new PageRequestDto(1, 1, List.of("summary"), List.of("asc"));
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI + "/summaries")
                                        .params(asParamsMap(pageRequest));
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
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI + "/summaries")
                                        .params(asParamsMap(pageRequest));
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
                        final var requestBuilder = MockMvcRequestBuilders
                                        .patch(CONVERSATIONS_URI + "/" + conversationId)
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
                        test_patch_validation(patch, "summary must not be blank or null");
                }

                @Test
                public void patch_conversation_summary_null_400() throws Exception {
                        final var patch = new JsonPatchDto("replace", "/summary", null);
                        test_patch_validation(patch, "summary must not be blank or null");
                }

                @Test
                public void patch_conversation_summary_too_short_400() throws Exception {
                        final var patch = new JsonPatchDto("replace", "/summary", "xx");
                        test_patch_validation(patch, "summary must have at least");
                }

                @Test
                public void patch_conversation_summary_too_long_400() throws Exception {
                        final var tooLongSummary = generateTooLongString(33);
                        final var patch = new JsonPatchDto("replace", "/summary", tooLongSummary);
                        test_patch_validation(patch, "summary must have at most");
                }

                @Test
                public void patch_conversation_not_patchable_field_400() throws Exception {
                        final var patch = new JsonPatchDto("replace", "/id", UUID.nameUUIDFromBytes("fake".getBytes()));
                        test_patch_validation(patch, "'id' is not patchable");
                }

                @Test
                public void patch_conversation_not_existing_field_400() throws Exception {
                        final var patch = new JsonPatchDto("add", "/owner", "johndoe");
                        test_patch_validation(patch, "'owner' does not exist in");
                }

                private void test_patch_validation(JsonPatchDto jsonPatchDto, String expectedError) throws Exception {
                        final var requestBuilder = MockMvcRequestBuilders
                                        .patch(CONVERSATIONS_URI + "/" + conversationId)
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
                        final var requestBuilder = MockMvcRequestBuilders
                                        .delete(CONVERSATIONS_URI + "/" + conversationId);
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isNoContent())
                                        .andExpect(content().string(""));
                }

                @Test
                public void delete_conversation_404() throws Exception {
                        final var notExistingId = UUID.nameUUIDFromBytes("not existing".getBytes());
                        final var requestBuilder = MockMvcRequestBuilders
                                        .delete(CONVERSATIONS_URI + "/" + notExistingId);
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isNotFound())
                                        .andExpect(jsonPath("$.errors[0]", containsString("Not found")));
                }
        }

        @Nested
        @DisplayName("Search forbidden")
        @WithMockUser(username = "user")
        class SearchForbidden {
                @Test
                public void get_conversations_403() throws Exception {
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI);
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isForbidden())
                                        .andExpect(jsonPath("$.errors[0]", containsString("Access Denied")));
                }
        }

        @Nested
        @DisplayName("Search")
        @WithMockUser(username = "mod", roles = "MOD")
        class Search {

                private Conversation userConv;
                private Conversation modConv;
                private Conversation adminConv;

                @BeforeAll
                public void beforeAll() {
                        userConv = createConversation("Some user conversation", "user");
                        userConv.addMessages(createMessages(userConv));
                        modConv = createConversation("Some mod conversation", "mod");
                        modConv.addMessages(createMessages(modConv));
                        adminConv = createConversation("Some admin conversation", "admin");
                        adminConv.addMessages(createMessages(adminConv));
                }

                @BeforeEach
                public void beforeEach() {
                        conversationCrudRepository.deleteAll();
                        conversationCrudRepository.saveAll(List.of(userConv, modConv, adminConv));
                }

                @Test
                public void search_no_filter() throws Exception {
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI);
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(3)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(3)));
                }

                @Test
                public void search_summary_filter() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().summary("user").build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(1)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(1)))
                                        .andExpect(jsonPath("$.items[0].id", is(userConv.getId().toString())))
                                        .andExpect(jsonPath("$.items[0].summary", is(userConv.getSummary())));
                }

                @Test
                public void search_by_user_firstname() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().userFullName("John").build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(3)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(3)));
                }

                @Test
                public void search_by_user_lastname() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder()
                                        .userFullName(userConv.getUser().getLastname())
                                        .build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(1)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(1)))
                                        .andExpect(jsonPath("$.items[0].id", is(userConv.getId().toString())))
                                        .andExpect(jsonPath(
                                                        "$.items[0].user.lastname",
                                                        is(userConv.getUser().getLastname())));
                }

                @Test
                public void search_by_user_fullName() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder()
                                        .userFullName(userConv.getUser().getLastname() + " "
                                                        + userConv.getUser().getFirstname())
                                        .build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(1)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(1)))
                                        .andExpect(jsonPath("$.items[0].id", is(userConv.getId().toString())))
                                        .andExpect(jsonPath(
                                                        "$.items[0].user.lastname",
                                                        is(userConv.getUser().getLastname())))
                                        .andExpect(jsonPath(
                                                        "$.items[0].user.firstname",
                                                        is(userConv.getUser().getFirstname())));
                }

                @Test
                public void search_by_deleted() throws Exception {
                        userConv.setDeleted(true);
                        conversationCrudRepository.save(userConv);

                        final var searchCriteria = ConversationCriteria.builder().deleted(true).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(1)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(1)))
                                        .andExpect(jsonPath("$.items[0].id", is(userConv.getId().toString())))
                                        .andExpect(jsonPath("$.items[0].deleted", is(true)));
                }

                @Test
                public void search_by_min_message_count_no_results() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().minMessageCount(3).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(0)))
                                        .andExpect(jsonPath("$.totalPages", is(0)))
                                        .andExpect(jsonPath("$.items", hasSize(0)));
                }

                @Test
                public void search_by_min_message_count_all_results() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().minMessageCount(1).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(3)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(3)));
                }

                @Test
                public void search_by_max_message_count_no_results() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().maxMessageCount(1).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(0)))
                                        .andExpect(jsonPath("$.totalPages", is(0)))
                                        .andExpect(jsonPath("$.items", hasSize(0)));
                }

                @Test
                public void search_by_max_message_count_all_results() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().maxMessageCount(3).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(3)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(3)));
                }

                @Test
                public void search_by_min_created_at_no_results() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().minCreatedAt(Instant.now()).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(0)))
                                        .andExpect(jsonPath("$.totalPages", is(0)))
                                        .andExpect(jsonPath("$.items", hasSize(0)));
                }

                @Test
                public void search_by_min_created_at_all_results() throws Exception {
                        final var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
                        final var searchCriteria = ConversationCriteria.builder().minCreatedAt(oneHourEarlier).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(3)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(3)));
                }

                @Test
                public void search_by_max_created_at_no_results() throws Exception {
                        final var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
                        final var searchCriteria = ConversationCriteria.builder().maxCreatedAt(oneHourEarlier).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(0)))
                                        .andExpect(jsonPath("$.totalPages", is(0)))
                                        .andExpect(jsonPath("$.items", hasSize(0)));
                }

                @Test
                public void search_by_max_created_at_all_results() throws Exception {
                        final var searchCriteria = ConversationCriteria.builder().maxCreatedAt(Instant.now()).build();
                        final var requestBuilder = MockMvcRequestBuilders.get(CONVERSATIONS_URI)
                                        .params(asParamsMap(searchCriteria));
                        mockMvc.perform(requestBuilder)
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.pageNumber", is(0)))
                                        .andExpect(jsonPath("$.totalElements", is(3)))
                                        .andExpect(jsonPath("$.totalPages", is(1)))
                                        .andExpect(jsonPath("$.items", hasSize(3)));
                }

                private Conversation createConversation(String summary, String username) {
                        final var conversationId = UUID.nameUUIDFromBytes(summary.getBytes());
                        final var user = userCrudRepository.findByUsername(username);
                        return Conversation.builder()
                                        .id(conversationId)
                                        .user(user.orElseThrow(
                                                        () -> new RuntimeException("User " + username + " not found")))
                                        .summary(summary)
                                        .build();
                }

                private List<MessageEntity> createMessages(Conversation conversation) {
                        final var userMessage = MessageEntity.builder()
                                        .conversation(conversation)
                                        .content("Hello!")
                                        .type(MessageType.USER)
                                        .build();
                        final var assistantMessage = MessageEntity.builder()
                                        .conversation(conversation)
                                        .content("Hi!")
                                        .type(MessageType.ASSISTANT)
                                        .build();
                        return List.of(userMessage, assistantMessage);
                }
        }
}
