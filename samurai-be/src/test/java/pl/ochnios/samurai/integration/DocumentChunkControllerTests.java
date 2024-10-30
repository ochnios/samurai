package pl.ochnios.samurai.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.samurai.TestUtils.asParamsMap;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.samurai.model.dtos.document.chunk.DocumentChunkCriteria;
import pl.ochnios.samurai.model.entities.document.chunk.DocumentChunk;
import pl.ochnios.samurai.model.seeders.DocumentChunkSeeder;
import pl.ochnios.samurai.model.seeders.DocumentSeeder;
import pl.ochnios.samurai.model.seeders.UserSeeder;
import pl.ochnios.samurai.repositories.impl.DocumentChunkCrudRepository;
import pl.ochnios.samurai.repositories.impl.DocumentCrudRepository;
import pl.ochnios.samurai.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
public class DocumentChunkControllerTests {

    private static final UUID DOCUMENT_ID = UUID.nameUUIDFromBytes("Sample PDF".getBytes());
    private static final String CHUNKS_URI = "/documents/" + DOCUMENT_ID + "/chunks";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private DocumentSeeder documentSeeder;

    @Autowired
    private DocumentChunkSeeder chunkSeeder;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Autowired
    private DocumentCrudRepository documentCrudRepository;

    @Autowired
    private DocumentChunkCrudRepository chunkCrudRepository;

    private List<DocumentChunk> chunks;

    @BeforeAll
    public void setup() {
        documentCrudRepository.deleteAll();
        userCrudRepository.deleteAll();
        userSeeder.seed();
    }

    @AfterAll
    public void tearDown() {
        documentCrudRepository.deleteAll();
        userCrudRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        documentCrudRepository.deleteAll();
        documentSeeder.seed();
        chunkSeeder.seed();

        chunks = chunkCrudRepository.findAllByDocumentId(DOCUMENT_ID).stream()
                .sorted(Comparator.comparingInt(DocumentChunk::getPosition))
                .toList();
    }

    @Nested
    @DisplayName("Patch")
    @WithMockUser(username = "mod", roles = "MOD")
    class Patch {

        // TODO chunk patch operation tests
    }

    @Nested
    @DisplayName("Delete")
    @WithMockUser(username = "mod", roles = "MOD")
    class Delete {

        // TODO chunk delete operation tests
    }

    @Nested
    @DisplayName("Search forbidden")
    @WithMockUser(username = "user")
    class SearchForbidden {
        @Test
        public void get_documents_403() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI);
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
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_content_filter() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().content("Table of contents").build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
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
            final var searchCriteria = DocumentChunkCriteria.builder()
                    .globalSearch("Table of contents")
                    .build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
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
            final var searchCriteria =
                    DocumentChunkCriteria.builder().minLength(1500).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_length_all_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().minLength(100).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_max_length_no_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().maxLength(50).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_length_all_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().maxLength(5000).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_min_position_no_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().minPosition(10).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_position_all_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().minPosition(0).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_max_position_no_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().maxPosition(-1).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_position_all_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().maxPosition(10).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_min_updated_at_no_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().minUpdatedAt(Instant.now()).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_updated_at_all_results() throws Exception {
            final var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            final var searchCriteria =
                    DocumentChunkCriteria.builder().minUpdatedAt(oneHourEarlier).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }

        @Test
        public void search_by_max_updated_at_no_results() throws Exception {
            final var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            final var searchCriteria =
                    DocumentChunkCriteria.builder().maxUpdatedAt(oneHourEarlier).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_updated_at_all_results() throws Exception {
            final var searchCriteria =
                    DocumentChunkCriteria.builder().maxUpdatedAt(Instant.now()).build();
            final var requestBuilder = MockMvcRequestBuilders.get(CHUNKS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(chunks.size())))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(chunks.size())));
        }
    }
}
