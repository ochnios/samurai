package pl.ochnios.samurai.integration;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.samurai.TestUtils.asJsonString;
import static pl.ochnios.samurai.TestUtils.asParamsMap;
import static pl.ochnios.samurai.TestUtils.generateTooLongString;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.commons.patch.JsonPatchDto;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkCriteria;
import pl.ochnios.samurai.model.dtos.document.chunk.ChunkDto;
import pl.ochnios.samurai.model.entities.document.chunk.Chunk;
import pl.ochnios.samurai.model.seeders.ChunkSeeder;
import pl.ochnios.samurai.model.seeders.DocumentSeeder;
import pl.ochnios.samurai.model.seeders.UserSeeder;
import pl.ochnios.samurai.repositories.impl.ChunkCrudRepository;
import pl.ochnios.samurai.repositories.impl.DocumentJpaRepository;
import pl.ochnios.samurai.repositories.impl.UserCrudRepository;
import pl.ochnios.samurai.services.EmbeddingService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
public class ChunkControllerTests {

    private static final UUID DOCUMENT_ID = UUID.nameUUIDFromBytes("Sample PDF".getBytes());
    private static final String CHUNKS_URI = "/documents/" + DOCUMENT_ID + "/chunks";

    @MockBean
    private EmbeddingService embeddingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private DocumentSeeder documentSeeder;

    @Autowired
    private ChunkSeeder chunkSeeder;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Autowired
    private DocumentJpaRepository documentJpaRepository;

    @Autowired
    private ChunkCrudRepository chunkCrudRepository;

    private List<Chunk> chunks;

    @BeforeAll
    public void setup() {
        userCrudRepository.deleteAll();
        userSeeder.seed();
    }

    @AfterAll
    public void tearDown() {
        documentJpaRepository.deleteAll();
        userCrudRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        documentJpaRepository.deleteAll();
        documentSeeder.seed();
        chunkSeeder.seed();

        chunks = chunkCrudRepository.findAllByDocumentIdOrderByPositionAsc(DOCUMENT_ID).stream()
                .sorted(Comparator.comparingInt(Chunk::getPosition))
                .toList();
    }

    @Nested
    @DisplayName("Add")
    @WithMockUser(username = "mod", roles = "MOD")
    class Add {

        private ChunkDto chunkDto;

        @BeforeEach
        public void beforeEach() {
            chunkDto = ChunkDto.builder()
                    .position(0)
                    .content("This is a test chunk content that meets minimum length requirement")
                    .build();
        }

        @Test
        public void add_200() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post(CHUNKS_URI)
                    .content(asJsonString(chunkDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(not(blankOrNullString()))))
                    .andExpect(jsonPath("$.documentId", is(DOCUMENT_ID.toString())))
                    .andExpect(jsonPath("$.position", is(chunkDto.getPosition())))
                    .andExpect(jsonPath("$.content", is(chunkDto.getContent())))
                    .andExpect(jsonPath("$.length", is(chunkDto.getContent().length())))
                    .andExpect(jsonPath("$.updatedAt", is(not(emptyOrNullString()))));
        }

        @Test
        public void add_content_too_short_400() throws Exception {
            chunkDto.setContent("Too short content");
            test_add_validation(chunkDto, "must have at least 20 characters");
        }

        @Test
        public void add_content_too_long_400() throws Exception {
            chunkDto.setContent(generateTooLongString(8193));
            test_add_validation(chunkDto, "must have at most 8192 characters");
        }

        @Test
        public void add_content_null_400() throws Exception {
            chunkDto.setContent(null);
            test_add_validation(chunkDto, "must not be blank or null");
        }

        @Test
        public void add_content_blank_400() throws Exception {
            chunkDto.setContent("    ");
            test_add_validation(chunkDto, "must have at least 20 characters");
        }

        @Test
        public void add_position_negative_400() throws Exception {
            chunkDto.setPosition(-1);
            test_add_validation(chunkDto, "must be greater than or equal to 0");
        }

        private void test_add_validation(ChunkDto chunkDto, String expectedError) throws Exception {
            var requestBuilder = MockMvcRequestBuilders.post(CHUNKS_URI)
                    .content(asJsonString(chunkDto))
                    .contentType(MediaType.APPLICATION_JSON);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString(expectedError)));
        }
    }

    @Nested
    @DisplayName("Patch")
    @WithMockUser(username = "mod", roles = "MOD")
    class Patch {

        @Test
        public void patch_chunk_content_200() throws Exception {
            var newContent = "This is updated content that meets the minimum length requirement for testing";
            var patch = new JsonPatchDto("replace", "/content", newContent);
            var requestBuilder = MockMvcRequestBuilders.patch(
                            CHUNKS_URI + "/" + chunks.getFirst().getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(chunks.getFirst().getId().toString())))
                    .andExpect(jsonPath("$.content", is(newContent)))
                    .andExpect(jsonPath("$.length", is(newContent.length())));
        }

        @Test
        public void patch_chunk_position_200() throws Exception {
            var newPosition = chunks.getFirst().getPosition() + 1;
            var patch = new JsonPatchDto("replace", "/position", newPosition);
            var requestBuilder = MockMvcRequestBuilders.patch(
                            CHUNKS_URI + "/" + chunks.getFirst().getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(chunks.getFirst().getId().toString())))
                    .andExpect(jsonPath("$.position", is(newPosition)));
        }

        @Test
        public void patch_chunk_content_too_short_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/content", "Too short");
            test_patch_validation(patch, "must have at least 20 characters");
        }

        @Test
        public void patch_chunk_content_too_long_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/content", generateTooLongString(8193));
            test_patch_validation(patch, "must have at most 8192 characters");
        }

        @Test
        public void patch_chunk_content_blank_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/content", "   ");
            test_patch_validation(patch, "must have at least 20 characters");
        }

        @Test
        public void patch_chunk_position_negative_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/position", -1);
            test_patch_validation(patch, "must be greater than or equal to 0");
        }

        @Test
        public void patch_chunk_position_out_of_range_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/position", chunks.size() + 1);
            test_patch_validation(patch, "Requested position is out of chunks range");
        }

        @Test
        public void patch_chunk_not_patchable_id_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/id", UUID.randomUUID());
            test_patch_validation(patch, "'id' is not patchable");
        }

        @Test
        public void patch_chunk_not_patchable_document_id_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/documentId", UUID.randomUUID());
            test_patch_validation(patch, "'documentId' is not patchable");
        }

        @Test
        public void patch_chunk_not_patchable_length400() throws Exception {
            var patch = new JsonPatchDto("replace", "/length", 100);
            test_patch_validation(patch, "'length' is not patchable");
        }

        @Test
        public void patch_chunk_not_patchable_update_at_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/updatedAt", Instant.now().toString());
            test_patch_validation(patch, "'updatedAt' is not patchable");
        }

        private void test_patch_validation(JsonPatchDto jsonPatchDto, String expectedError) throws Exception {
            var requestBuilder = MockMvcRequestBuilders.patch(
                            CHUNKS_URI + "/" + chunks.getFirst().getId())
                    .content(asJsonString(Set.of(jsonPatchDto)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString(expectedError)));
        }
    }

    @Nested
    @DisplayName("Delete")
    @WithMockUser(username = "mod", roles = "MOD")
    class Delete {

        @Test
        public void delete_chunk_204() throws Exception {
            var chunkId = chunks.getFirst().getId();
            var requestBuilder = MockMvcRequestBuilders.delete(CHUNKS_URI + "/" + chunkId);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        public void delete_chunk_404() throws Exception {
            var notExistingId = UUID.randomUUID();
            var requestBuilder = MockMvcRequestBuilders.delete(CHUNKS_URI + "/" + notExistingId);
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
        public void get_documents_403() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0]", containsString("Access Denied")));
        }
    }

    @Nested
    @DisplayName("Search")
    @WithMockUser(username = "mod", roles = "MOD")
    class Search {

        @Test
        public void search_no_filter() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_content_filter() throws Exception {
            var searchCriteria =
                    ChunkCriteria.builder().content("Table of contents").build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath(
                            "$.items[0].id", is(chunks.getFirst().getId().toString())))
                    .andExpect(
                            jsonPath("$.items[0].position", is(chunks.getFirst().getPosition())))
                    .andExpect(
                            jsonPath("$.items[0].content", is(chunks.getFirst().getContent())));
        }

        @Test
        public void search_by_global_filter() throws Exception {
            var searchCriteria =
                    ChunkCriteria.builder().globalSearch("Table of contents").build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath(
                            "$.items[0].id", is(chunks.getFirst().getId().toString())))
                    .andExpect(
                            jsonPath("$.items[0].position", is(chunks.getFirst().getPosition())))
                    .andExpect(
                            jsonPath("$.items[0].content", is(chunks.getFirst().getContent())));
        }

        @Test
        public void search_by_min_length_no_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().minLength(1500).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_length_all_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().minLength(100).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_max_length_no_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().maxLength(50).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_length_all_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().maxLength(5000).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_min_position_no_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().minPosition(10).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_position_all_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().minPosition(0).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_max_position_no_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().maxPosition(-1).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_position_all_results() throws Exception {
            var searchCriteria = ChunkCriteria.builder().maxPosition(10).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_min_updated_at_no_results() throws Exception {
            var searchCriteria =
                    ChunkCriteria.builder().minUpdatedAt(Instant.now()).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_updated_at_all_results() throws Exception {
            var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            var searchCriteria =
                    ChunkCriteria.builder().minUpdatedAt(oneHourEarlier).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_max_updated_at_no_results() throws Exception {
            var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            var searchCriteria =
                    ChunkCriteria.builder().maxUpdatedAt(oneHourEarlier).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_updated_at_all_results() throws Exception {
            var searchCriteria =
                    ChunkCriteria.builder().maxUpdatedAt(Instant.now()).build();
            var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }
    }
}
