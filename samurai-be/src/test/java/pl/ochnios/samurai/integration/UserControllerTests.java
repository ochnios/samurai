package pl.ochnios.samurai.integration;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.samurai.TestUtils.asJsonString;
import static pl.ochnios.samurai.TestUtils.asParamsMap;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
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
import pl.ochnios.samurai.commons.AppConstants;
import pl.ochnios.samurai.commons.patch.JsonPatchDto;
import pl.ochnios.samurai.model.dtos.user.UserCriteria;
import pl.ochnios.samurai.model.entities.user.Role;
import pl.ochnios.samurai.model.seeders.UserSeeder;
import pl.ochnios.samurai.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
public class UserControllerTests {

    private static final String USERS_URI = "/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @BeforeEach
    public void beforeEach() {
        userCrudRepository.deleteAll();
        userSeeder.seed();
    }

    @AfterAll
    public void tearDown() {
        userCrudRepository.deleteAll();
    }

    @Nested
    @DisplayName("Search forbidden")
    @WithMockUser(username = "user")
    class SearchForbidden {
        @Test
        public void get_users_403() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0]", containsString("Access Denied")));
        }

        @Test
        public void get_user_403() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI + "/user");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0]", containsString("Access Denied")));
        }

        @Test
        public void patch_user_403() throws Exception {
            var patch = new JsonPatchDto("replace", "/role", Role.Admin);
            var requestBuilder = MockMvcRequestBuilders.patch(USERS_URI + "/user")
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.errors[0]", containsString("Access Denied")));
        }
    }

    @Nested
    @DisplayName("Search")
    @WithMockUser(username = "admin", roles = "ADMIN")
    class Search {

        @Test
        public void search_no_filter() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(3)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(3)));
        }

        @Test
        public void search_by_global_search() throws Exception {
            var searchCriteria = UserCriteria.builder().globalSearch("john").build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(3)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(3)));
        }

        @Test
        public void search_by_firstname() throws Exception {
            var searchCriteria = UserCriteria.builder().firstname("John").build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(3)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(3)));
        }

        @Test
        public void search_by_lastname() throws Exception {
            var searchCriteria = UserCriteria.builder().lastname("Admin").build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].lastname", is("Admin")));
        }

        @Test
        public void search_by_email() throws Exception {
            var searchCriteria = UserCriteria.builder().email("admin@users.com").build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].email", is("admin@users.com")));
        }

        @Test
        public void search_by_role() throws Exception {
            var searchCriteria = UserCriteria.builder().role(Role.Admin).build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(1)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(1)))
                    .andExpect(jsonPath("$.items[0].role", is("Admin")));
        }

        @Test
        public void search_by_min_created_at_no_results() throws Exception {
            var searchCriteria =
                    UserCriteria.builder().minCreatedAt(Instant.now()).build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
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
                    UserCriteria.builder().minCreatedAt(oneHourEarlier).build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(3)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(3)));
        }

        @Test
        public void search_by_max_created_at_no_results() throws Exception {
            var oneHourEarlier = Instant.now().minus(Duration.ofHours(1));
            var searchCriteria =
                    UserCriteria.builder().maxCreatedAt(oneHourEarlier).build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
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
                    UserCriteria.builder().maxCreatedAt(Instant.now()).build();
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI).params(asParamsMap(searchCriteria));
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageNumber", is(0)))
                    .andExpect(jsonPath("$.totalElements", is(3)))
                    .andExpect(jsonPath("$.totalPages", is(1)))
                    .andExpect(jsonPath("$.items", hasSize(3)));
        }
    }

    @Nested
    @DisplayName("Get")
    @WithMockUser(username = "admin", roles = "ADMIN")
    class Get {

        @Test
        public void get_user_200() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI + "/user");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("user")))
                    .andExpect(jsonPath("$.firstname", is("John")))
                    .andExpect(jsonPath("$.lastname", is("User")))
                    .andExpect(jsonPath("$.email", is("user@users.com")))
                    .andExpect(jsonPath("$.role", is("User")))
                    .andExpect(jsonPath("$.createdAt", not(blankOrNullString())));
        }

        @Test
        public void get_user_404() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(USERS_URI + "/nonexistent");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0]", containsString("Not found")));
        }
    }

    @Nested
    @DisplayName("Patch")
    @WithMockUser(username = "admin", roles = "ADMIN")
    class Patch {

        @Test
        public void patch_user_200() throws Exception {
            var patch = new JsonPatchDto("replace", "/role", Role.Mod);
            var requestBuilder = MockMvcRequestBuilders.patch(USERS_URI + "/user")
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("user")))
                    .andExpect(jsonPath("$.role", is("Mod")));
        }

        @Test
        public void patch_user_not_patchable_field_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/username", "newusername");
            var requestBuilder = MockMvcRequestBuilders.patch(USERS_URI + "/user")
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("'username' is not patchable")));
        }

        @Test
        public void patch_user_not_existing_field_400() throws Exception {
            var patch = new JsonPatchDto("replace", "/nonexistent", "value");
            var requestBuilder = MockMvcRequestBuilders.patch(USERS_URI + "/user")
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("'nonexistent' does not exist")));
        }

        @Test
        public void patch_user_404() throws Exception {
            var patch = new JsonPatchDto("replace", "/role", Role.Mod);
            var requestBuilder = MockMvcRequestBuilders.patch(USERS_URI + "/nonexistent")
                    .content(asJsonString(Set.of(patch)))
                    .contentType(AppConstants.PATCH_MEDIA_TYPE);
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.errors[0]", containsString("Not found")));
        }
    }
}
