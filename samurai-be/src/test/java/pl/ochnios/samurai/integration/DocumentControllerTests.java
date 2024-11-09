package pl.ochnios.samurai.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.commons.patch.JsonPatchDto;
import pl.ochnios.samurai.model.dtos.document.DocumentCriteria;
import pl.ochnios.samurai.model.dtos.document.DocumentUploadDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.model.entities.document.DocumentStatus;
import pl.ochnios.samurai.model.seeders.DocumentSeeder;
import pl.ochnios.samurai.model.seeders.UserSeeder;
import pl.ochnios.samurai.repositories.impl.DocumentJpaRepository;
import pl.ochnios.samurai.repositories.impl.UserCrudRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.samurai.TestUtils.asJsonString;
import static pl.ochnios.samurai.TestUtils.asParamsMap;
import static pl.ochnios.samurai.TestUtils.generateTooLongString;
import static pl.ochnios.samurai.model.entities.document.DocumentStatus.ACTIVE;
import static pl.ochnios.samurai.model.entities.document.DocumentStatus.FAILED;
import static pl.ochnios.samurai.model.entities.document.DocumentStatus.IN_PROGRESS;
import static pl.ochnios.samurai.model.entities.document.DocumentStatus.UPLOADED;

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
    private DocumentJpaRepository documentJpaRepository;

    private DocumentEntity samplePDF;
    private DocumentEntity sampleDOCX;

    @BeforeAll
    public void setup() {
        documentJpaRepository.deleteAll();
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

        samplePDF = documentJpaRepository
                .findById(UUID.nameUUIDFromBytes("Sample PDF".getBytes()))
                .orElseThrow(() -> new RuntimeException("Sample PDF not found"));

        sampleDOCX = documentJpaRepository
                .findById(UUID.nameUUIDFromBytes("Sample DOCX".getBytes()))
                .orElseThrow(() -> new RuntimeException("Sample DOCX not found"));
    }

    @Nested
    @DisplayName("Upload")
    @WithMockUser(username = "mod", roles = "MOD")
    class Upload {

        private DocumentUploadDto documentUploadDto;

        @BeforeEach
        public void beforeEach() {
            var file = new MockMultipartFile("file", "test.txt", "text/plain", "Test file content".getBytes());
            documentUploadDto = new DocumentUploadDto(file, null, "Test document title", "Test document description");
        }

        @Test
        public void upload_200() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.multipart(DOCUMENTS_URI)
                    .file((MockMultipartFile) documentUploadDto.getFile())
                    .param("title", documentUploadDto.getTitle())
                    .param("description", documentUploadDto.getDescription());

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(not(blankOrNullString()))))
                    .andExpect(jsonPath("$.name", is(documentUploadDto.getFile().getOriginalFilename())))
                    .andExpect(jsonPath(
                            "$.size", is((int) documentUploadDto.getFile().getSize())))
                    .andExpect(jsonPath("$.user.username", is("mod")))
                    .andExpect(jsonPath("$.title", is(documentUploadDto.getTitle())))
                    .andExpect(jsonPath("$.description", is(documentUploadDto.getDescription())))
                    .andExpect(jsonPath("$.status", is(UPLOADED.name())))
                    .andExpect(jsonPath("$.createdAt", is(not(emptyOrNullString()))));
        }

        @Test
        public void upload_file_too_big_400() throws Exception {
            var content = generateTooLongString(50 * 1024 * 1024 + 1);
            var tooBigFile = new MockMultipartFile("file", "test.txt", "text/plain", content.getBytes());
            documentUploadDto.setFile(tooBigFile);
            test_upload_validation(documentUploadDto, "size must not be greater than");
        }

        @Test
        public void upload_title_too_short_400() throws Exception {
            documentUploadDto.setTitle("Aa");
            test_upload_validation(documentUploadDto, "must have at least 3 characters");
        }

        @Test
        public void upload_title_too_long_400() throws Exception {
            documentUploadDto.setTitle(generateTooLongString(256));
            test_upload_validation(documentUploadDto, "must have at most 255 characters");
        }

        @Test
        public void upload_title_null_400() throws Exception {
            documentUploadDto.setTitle(null);
            test_upload_validation(documentUploadDto, "must not be blank or null");
        }

        @Test
        public void upload_description_too_short_400() throws Exception {
            documentUploadDto.setDescription("Aa");
            test_upload_validation(documentUploadDto, "must have at least 3 characters");
        }

        @Test
        public void upload_description_too_long_400() throws Exception {
            documentUploadDto.setDescription(generateTooLongString(2049));
            test_upload_validation(documentUploadDto, "must have at most 2048 characters");
        }

        private void test_upload_validation(DocumentUploadDto documentUploadDto, String expectedError)
                throws Exception {
            var requestBuilder = MockMvcRequestBuilders.multipart(DOCUMENTS_URI)
                    .file((MockMultipartFile) (documentUploadDto.getFile()))
                    .param("title", documentUploadDto.getTitle())
                    .param("description", documentUploadDto.getDescription());
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString(expectedError)));
        }

        // TODO test description auto generation
    }

    @Nested
    @DisplayName("Download")
    @WithMockUser(username = "user")
    class Download {

        @Test
        public void download_by_id_200() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI + "/" + samplePDF.getId() + "/download");
            mockMvc.perform(requestBuilder)
                    .andExpect(header().string(
                                    HttpHeaders.CONTENT_DISPOSITION,
                                    "form-data; name=\"attachment\"; filename*=UTF-8''" + samplePDF.getName()))
                    .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                    .andExpect(header().string(HttpHeaders.CONTENT_LENGTH, Long.toString(samplePDF.getSize())));
        }
    }

    @Nested
    @DisplayName("Get")
    @WithMockUser(username = "mod", roles = "MOD")
    class Get {

        @Test
        public void get_documents_200() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void get_documents_2nd_page_200() throws Exception {
            var pageRequest = new PageRequestDto(1, 1, List.of("title"), List.of("asc"));
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(pageRequest));
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
                            "$.items[0].user.username", is(samplePDF.getUser().getUsername())))
                    .andExpect(jsonPath("$.items[0].title", is(samplePDF.getTitle())))
                    .andExpect(jsonPath("$.items[0].description", is(samplePDF.getDescription())))
                    .andExpect(jsonPath(
                            "$.items[0].status", is(samplePDF.getStatus().toString())))
                    .andExpect(jsonPath(
                            "$.items[0].createdAt", is(samplePDF.getCreatedAt().toString())));
        }

        @Test
        public void get_documents_2nd_page_empty_200() throws Exception {
            var pageRequest = new PageRequestDto(1, 5, null, null);
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(pageRequest));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(1)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void get_by_id_200() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI + "/" + sampleDOCX.getId());
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(sampleDOCX.getId().toString())))
                    .andExpect(jsonPath("$.name", is(sampleDOCX.getName())))
                    .andExpect(jsonPath("$.size", is((int) sampleDOCX.getSize())))
                    .andExpect(
                            jsonPath("$.user.username", is(sampleDOCX.getUser().getUsername())))
                    .andExpect(jsonPath("$.title", is(sampleDOCX.getTitle())))
                    .andExpect(jsonPath("$.description", is(sampleDOCX.getDescription())))
                    .andExpect(jsonPath("$.status", is(sampleDOCX.getStatus().toString())))
                    .andExpect(
                            jsonPath("$.createdAt", is(sampleDOCX.getCreatedAt().toString())));
        }

        @Test
        public void get_by_id_404() throws Exception {
            var notExistingId = UUID.nameUUIDFromBytes("not existing".getBytes());
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI + "/" + notExistingId);
            mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Patch")
    @WithMockUser(username = "mod", roles = "MOD")
    class Patch {

        @Test
        public void patch_document_title_200() throws Exception {
            var patch = new JsonPatchDto("replace", "/title", "New title");
            var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.title", is(patch.getValue())))
                    .andExpect(
                            jsonPath("$.createdAt", is(samplePDF.getCreatedAt().toString())));
        }

        @Test
        public void patch_document_title_blank_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/title", "   ");
            test_patch_validation(patch, "title must not be blank or null");
        }

        @Test
        public void patch_document_title_null_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/title", null);
            test_patch_validation(patch, "title must not be blank or null");
        }

        @Test
        public void patch_document_title_too_short_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/title", "xx");
            test_patch_validation(patch, "title must have at least");
        }

        @Test
        public void patch_document_title_long_400() throws Exception {
            var tooLongTitle = generateTooLongString(256);
            var patch = new JsonPatchDto("replace", "/title", tooLongTitle);
            test_patch_validation(patch, "title must have at most");
        }

        @Test
        public void patch_document_description_200() throws Exception {
            var patch = new JsonPatchDto("replace", "/description", "New description");
            var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.description", is(patch.getValue())))
                    .andExpect(
                            jsonPath("$.createdAt", is(samplePDF.getCreatedAt().toString())));
        }

        @Test
        public void patch_document_description_too_short_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/description", "xx");
            test_patch_validation(patch, "description must have at least");
        }

        @Test
        public void patch_document_description_too_long_400() throws Exception {
            var tooLongDescription = generateTooLongString(2049);
            var patch = new JsonPatchDto("replace", "/description", tooLongDescription);
            test_patch_validation(patch, "description must have at most");
        }

        @Test
        public void patch_document_status_to_active_200() throws Exception {
            samplePDF.setStatus(DocumentStatus.ARCHIVED);
            documentJpaRepository.save(samplePDF);

            var patch = new JsonPatchDto("replace", "/status", ACTIVE.name());
            var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.status", is(patch.getValue())))
                    .andExpect(
                            jsonPath("$.createdAt", is(samplePDF.getCreatedAt().toString())));
        }

        @Test
        public void patch_document_status_to_archived_200() throws Exception {
            samplePDF.setStatus(ACTIVE);
            documentJpaRepository.save(samplePDF);

            var patch = new JsonPatchDto("replace", "/status", DocumentStatus.ARCHIVED.name());
            var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.status", is(patch.getValue())))
                    .andExpect(
                            jsonPath("$.createdAt", is(samplePDF.getCreatedAt().toString())));
        }

        @Test
        public void patch_document_status_to_in_progress_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/status", IN_PROGRESS.name());
            test_patch_validation(patch, "cannot be assigned manually");
        }

        @Test
        public void patch_document_status_to_failed_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/status", DocumentStatus.FAILED.name());
            test_patch_validation(patch, "cannot be assigned manually");
        }

        @Test
        public void patch_document_status_from_uploaded_400() throws Exception {
            samplePDF.setStatus(UPLOADED);
            documentJpaRepository.save(samplePDF);
            var patch = new JsonPatchDto("replace", "/status", ACTIVE.name());
            test_patch_validation(patch, "cannot be changed manually");
        }

        @Test
        public void patch_document_status_from_failed_400() throws Exception {
            samplePDF.setStatus(FAILED);
            documentJpaRepository.save(samplePDF);
            var patch = new JsonPatchDto("replace", "/status", ACTIVE.name());
            test_patch_validation(patch, "cannot be changed manually");
        }

        @Test
        public void patch_document_status_from_in_progress_400() throws Exception {
            samplePDF.setStatus(IN_PROGRESS);
            documentJpaRepository.save(samplePDF);
            var patch = new JsonPatchDto("replace", "/status", ACTIVE.name());
            test_patch_validation(patch, "cannot be changed manually");
        }

        @Test
        public void patch_document_not_patchable_field_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/id", UUID.nameUUIDFromBytes("fake".getBytes()));
            test_patch_validation(patch, "'id' is not patchable");
        }

        @Test
        public void patch_document_not_existing_field_400() throws Exception {
            var patch = new JsonPatchDto("add", "/owner", "johndoe");
            test_patch_validation(patch, "'owner' does not exist in");
        }

        private void test_patch_validation(JsonPatchDto jsonPatchDto, String expectedError) throws Exception {
            var requestBuilder = MockMvcRequestBuilders.patch(DOCUMENTS_URI + "/" + samplePDF.getId())
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
            var requestBuilder = MockMvcRequestBuilders.delete(DOCUMENTS_URI + "/" + samplePDF.getId());
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
            var document = documentJpaRepository.findById(samplePDF.getId());
            assertThat(document, is(Optional.empty()));
        }

        @Test
        public void delete_document_404() throws Exception {
            var notExistingId = UUID.nameUUIDFromBytes("not existing".getBytes());
            var requestBuilder = MockMvcRequestBuilders.delete(DOCUMENTS_URI + "/" + notExistingId);
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
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI);
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
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_name_filter() throws Exception {
            var searchCriteria = DocumentCriteria.builder().filename(".pdf").build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
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
            var searchCriteria = DocumentCriteria.builder().userFullName("John").build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_user_lastname() throws Exception {
            var searchCriteria =
                    DocumentCriteria.builder().userFullName("Admin").build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].user.lastname", is("Admin")));
        }

        @Test
        public void search_by_user_fullName() throws Exception {
            var searchCriteria =
                    DocumentCriteria.builder().userFullName("Admin John").build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].user.lastname", is("Admin")))
                    .andExpect(jsonPath("$.items[0].user.firstname", is("John")));
        }

        @Test
        public void search_by_status() throws Exception {
            samplePDF.setStatus(ACTIVE);
            documentJpaRepository.save(samplePDF);

            var searchCriteria =
                    DocumentCriteria.builder().status(ACTIVE).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].id", is(samplePDF.getId().toString())))
                    .andExpect(jsonPath("$.items[0].status", is(ACTIVE.name())));
        }

        @Test
        public void search_by_min_size_no_results() throws Exception {
            var searchCriteria = DocumentCriteria.builder().minSize(1000_000L).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_size_all_results() throws Exception {
            var searchCriteria = DocumentCriteria.builder().minSize(100_000L).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_max_size_no_results() throws Exception {
            var searchCriteria = DocumentCriteria.builder().maxSize(100_000L).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_size_all_results() throws Exception {
            var searchCriteria = DocumentCriteria.builder().maxSize(1000_000L).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_min_created_at_no_results() throws Exception {
            var searchCriteria =
                    DocumentCriteria.builder().minCreatedAt(Instant.now()).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_min_created_at_all_results() throws Exception {
            var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            var searchCriteria =
                    DocumentCriteria.builder().minCreatedAt(oneHourEarlier).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }

        @Test
        public void search_by_max_created_at_no_results() throws Exception {
            var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            var searchCriteria =
                    DocumentCriteria.builder().maxCreatedAt(oneHourEarlier).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(0)))
                    .andExpect(jsonPath("$.totalPages", is(0)))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        public void search_by_max_created_at_all_results() throws Exception {
            var searchCriteria =
                    DocumentCriteria.builder().maxCreatedAt(Instant.now()).build();
            var requestBuilder = MockMvcRequestBuilders.get(DOCUMENTS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(2)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(2)));
        }
    }
}
