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

import java.time.Duration;
import java.time.Instant;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.ninjabe.commons.AppConstants;
import pl.ochnios.ninjabe.commons.patch.JsonPatchDto;
import pl.ochnios.ninjabe.model.dtos.document.DocumentCriteria;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;
import pl.ochnios.ninjabe.model.entities.document.DocumentEntity;
import pl.ochnios.ninjabe.model.entities.document.DocumentStatus;
import pl.ochnios.ninjabe.model.seeders.DocumentSeeder;
import pl.ochnios.ninjabe.model.seeders.UserSeeder;
import pl.ochnios.ninjabe.repositories.impl.DocumentCrudRepository;
import pl.ochnios.ninjabe.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
public class DocumentControllerTests {

    private static final String DOCUMENTS_URI = "/documents";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private DocumentSeeder documentSeeder;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Autowired
    private DocumentCrudRepository documentCrudRepository;

    private DocumentEntity samplePDF;
    private DocumentEntity sampleDOCX;

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

        samplePDF = documentCrudRepository
                .findById(UUID.nameUUIDFromBytes("Sample PDF".getBytes()))
                .orElseThrow(() -> new RuntimeException("Sample PDF not found"));

        sampleDOCX = documentCrudRepository
                .findById(UUID.nameUUIDFromBytes("Sample DOCX".getBytes()))
                .orElseThrow(() -> new RuntimeException("Sample DOCX not found"));
    }

    @Nested
    @DisplayName("Upload")
    @WithMockUser(username = "mod", roles = "MOD")
    class Upload {
        // TODO
    }

    @Nested
    @DisplayName("Download")
    @WithMockUser(username = "mod", roles = "MOD")
    class Download {
        // TODO
    }

    @Nested
    @DisplayName("Get")
    @WithMockUser(username = "mod", roles = "MOD")
    class Get {

        @Test
        public void get_documents_200() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void get_documents_2nd_page_200() throws Exception {
            final var pageRequest = new PageRequestDto(1, 1, List.of("title"), List.of("asc"));
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(pageRequest));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(1)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(2)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].name", is(samplePDF.getName())))
                    .andExpect(jsonPath("$.items[0].size", is((int) samplePDF.getSize())))
                    .andExpect(jsonPath(
                            "$.items[0].uploader.username",
                            is(samplePDF.getUploader().getUsername())))
                    .andExpect(jsonPath("$.items[0].title", is(samplePDF.getTitle())))
                    .andExpect(jsonPath("$.items[0].description", is(samplePDF.getDescription())))
                    .andExpect(jsonPath(
                            "$.items[0].status", is(samplePDF.getStatus().toString())))
                    .andExpect(jsonPath(
                            "$.items[0].createdAt", is(samplePDF.getCreatedAt().toString())));
        }

        @Test
        public void get_documents_2nd_page_empty_200() throws Exception {
            final var pageRequest = new PageRequestDto(1, 5, null, null);
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(pageRequest));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(1)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void get_by_id_200() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI + "/" + sampleDOCX.getId());
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(sampleDOCX.getId().toString())))
                    .andExpect(jsonPath("$.name", is(sampleDOCX.getName())))
                    .andExpect(jsonPath("$.size", is((int) sampleDOCX.getSize())))
                    .andExpect(jsonPath(
                            "$.uploader.username", is(sampleDOCX.getUploader().getUsername())))
                    .andExpect(jsonPath("$.title", is(sampleDOCX.getTitle())))
                    .andExpect(jsonPath("$.description", is(sampleDOCX.getDescription())))
                    .andExpect(jsonPath("$.status", is(sampleDOCX.getStatus().toString())))
                    .andExpect(
                            jsonPath("$.createdAt", is(sampleDOCX.getCreatedAt().toString())));
        }

        @Test
        public void get_by_id_404() throws Exception {
            final var notExistingId = UUID.nameUUIDFromBytes("not existing".getBytes());
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI + "/" + notExistingId);
            mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Patch")
    @WithMockUser(username = "mod", roles = "MOD")
    class Patch {

        @Test
        public void patch_document_title_200() throws Exception {
            final var patch = new JsonPatchDto("replace", "/title", "New title");
            final var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.title", is(patch.getValue())))
                    .andExpect(jsonPath("$.createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void patch_document_title_blank_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/title", "   ");
            test_validation(patch, "title must not be blank or null");
        }

        @Test
        public void patch_document_title_null_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/title", null);
            test_validation(patch, "title must not be blank or null");
        }

        @Test
        public void patch_document_title_too_short_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/title", "xx");
            test_validation(patch, "title must have at least");
        }

        @Test
        public void patch_document_title_long_400() throws Exception {
            final var tooLongTitle = generateTooLongString(256);
            final var patch = new JsonPatchDto("replace", "/title", tooLongTitle);
            test_validation(patch, "title must have at most");
        }

        @Test
        public void patch_document_description_200() throws Exception {
            final var patch = new JsonPatchDto("replace", "/description", "New description");
            final var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.description", is(patch.getValue())))
                    .andExpect(jsonPath("$.createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void patch_document_description_too_short_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/description", "xx");
            test_validation(patch, "description must have at least");
        }

        @Test
        public void patch_document_description_too_long_400() throws Exception {
            final var tooLongDescription = generateTooLongString(2049);
            final var patch = new JsonPatchDto("replace", "/description", tooLongDescription);
            test_validation(patch, "description must have at most");
        }

        @Test
        public void patch_document_status_to_active_200() throws Exception {
            samplePDF.setStatus(DocumentStatus.INACTIVE);
            documentCrudRepository.save(samplePDF);

            final var patch = new JsonPatchDto("replace", "/status", DocumentStatus.ACTIVE.name());
            final var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.status", is(patch.getValue())))
                    .andExpect(jsonPath("$.createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void patch_document_status_to_inactive_200() throws Exception {
            samplePDF.setStatus(DocumentStatus.ACTIVE);
            documentCrudRepository.save(samplePDF);

            final var patch = new JsonPatchDto("replace", "/status", DocumentStatus.INACTIVE.name());
            final var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.status", is(patch.getValue())))
                    .andExpect(jsonPath("$.createdAt", is(not(blankOrNullString()))));
        }

        @Test
        public void patch_document_status_to_uploaded_400() throws Exception {
            samplePDF.setStatus(DocumentStatus.ACTIVE);
            documentCrudRepository.save(samplePDF);
            final var patch = new JsonPatchDto("replace", "/status", DocumentStatus.UPLOADED.name());
            test_validation(patch, "cannot be assigned manually");
        }

        @Test
        public void patch_document_status_to_failed_400() throws Exception {
            samplePDF.setStatus(DocumentStatus.ACTIVE);
            documentCrudRepository.save(samplePDF);
            final var patch = new JsonPatchDto("replace", "/status", DocumentStatus.FAILED.name());
            test_validation(patch, "cannot be assigned manually");
        }

        @Test
        public void patch_document_status_from_uploaded_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/status", DocumentStatus.ACTIVE.name());
            test_validation(patch, "cannot be changed manually");
        }

        @Test
        public void patch_document_status_from_failed_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/status", DocumentStatus.ACTIVE.name());
            test_validation(patch, "cannot be changed manually");
        }

        @Test
        public void patch_document_not_patchable_field_400() throws Exception {
            final var patch = new JsonPatchDto("replace", "/id", UUID.nameUUIDFromBytes("fake".getBytes()));
            test_validation(patch, "'id' is not patchable");
        }

        @Test
        public void patch_document_not_existing_field_400() throws Exception {
            final var patch = new JsonPatchDto("add", "/owner", "johndoe");
            test_validation(patch, "'owner' does not exist in");
        }

        private void test_validation(JsonPatchDto jsonPatchDto, String expectedError) throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
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
        public void delete_document_204() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.delete(DOCUMENTS_URI + "/" + samplePDF.getId());
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
        }

        @Test
        public void delete_document_404() throws Exception {
            final var notExistingId = UUID.nameUUIDFromBytes("not existing".getBytes());
            final var requestBuilder = MockMvcRequestBuilders.delete(DOCUMENTS_URI + "/" + notExistingId);
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
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI);
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
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_name_filter() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().filename(".pdf").build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].name", is(samplePDF.getName())));
        }

        @Test
        public void search_by_user_firstname() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().userFullName("John").build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_user_lastname() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().userFullName("Admin").build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].uploader.lastname", is("Admin")));
        }

        @Test
        public void search_by_user_fullName() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().userFullName("Admin John").build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].uploader.lastname", is("Admin")))
                    .andExpect(jsonPath("$.items[0].uploader.firstname", is("John")));
        }

        @Test
        public void search_by_status() throws Exception {
            samplePDF.setStatus(DocumentStatus.ACTIVE);
            documentCrudRepository.save(samplePDF);

            final var searchCriteria =
                    DocumentCriteria.builder().status(DocumentStatus.ACTIVE).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].status", is(DocumentStatus.ACTIVE.name())));
        }

        @Test
        public void search_by_min_size_no_results() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().minSize(1000_000L).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_size_all_results() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().minSize(100_000L).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_max_size_no_results() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().maxSize(100_000L).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_size_all_results() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().maxSize(1000_000L).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_min_created_at_no_results() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().minCreatedAt(Instant.now()).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
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
            final var searchCriteria =
                    DocumentCriteria.builder().minCreatedAt(oneHourEarlier).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_max_created_at_no_results() throws Exception {
            final var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            final var searchCriteria =
                    DocumentCriteria.builder().maxCreatedAt(oneHourEarlier).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_created_at_all_results() throws Exception {
            final var searchCriteria =
                    DocumentCriteria.builder().maxCreatedAt(Instant.now()).build();
            final var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }
    }
}
