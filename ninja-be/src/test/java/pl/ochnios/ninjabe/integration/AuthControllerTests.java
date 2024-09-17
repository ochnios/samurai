package pl.ochnios.ninjabe.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static pl.ochnios.ninjabe.TestUtils.asJsonString;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import pl.ochnios.ninjabe.model.dtos.auth.LoginDto;
import pl.ochnios.ninjabe.model.entities.user.Role;
import pl.ochnios.ninjabe.model.seeders.UserSeeder;
import pl.ochnios.ninjabe.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    private static final String AUTH_URL = "/auth";

    @Value("${custom.jwt.cookie.name}")
    private String jwtCookieName;

    @Value("${custom.jwt.cookie.prefix}")
    private String jwtPrefix;

    @Autowired private MockMvc mockMvc;
    @Autowired private UserCrudRepository userCrudRepository;
    @Autowired private UserSeeder userSeeder;

    @BeforeAll
    public void setup() {
        userCrudRepository.deleteAll();
        userSeeder.seed();
    }

    @Nested
    @DisplayName("Login")
    class Login {

        @Test
        void login_as_user_200() throws Exception {
            final var loginDto = new LoginDto("user", "user");
            final var requestBuilder =
                    MockMvcRequestBuilders.post(AUTH_URL + "/login")
                            .content(asJsonString(loginDto))
                            .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("user")))
                    .andExpect(jsonPath("$.email", is("user@users.com")))
                    .andExpect(jsonPath("$.role", is(Role.User.name())))
                    .andExpect(cookie().value(jwtCookieName, startsWith(jwtPrefix)));
        }

        @Test
        void login_as_mod_200() throws Exception {
            final var loginDto = new LoginDto("mod", "mod");
            final var requestBuilder =
                    MockMvcRequestBuilders.post(AUTH_URL + "/login")
                            .content(asJsonString(loginDto))
                            .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("mod")))
                    .andExpect(jsonPath("$.email", is("mod@users.com")))
                    .andExpect(jsonPath("$.role", is(Role.Mod.name())))
                    .andExpect(cookie().value(jwtCookieName, startsWith(jwtPrefix)));
        }

        @Test
        void login_as_admin_200() throws Exception {
            final var loginDto = new LoginDto("admin", "admin");
            final var requestBuilder =
                    MockMvcRequestBuilders.post(AUTH_URL + "/login")
                            .content(asJsonString(loginDto))
                            .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("admin")))
                    .andExpect(jsonPath("$.email", is("admin@users.com")))
                    .andExpect(jsonPath("$.role", is(Role.Admin.name())))
                    .andExpect(cookie().value(jwtCookieName, startsWith(jwtPrefix)));
        }

        @Test
        void login_bad_username_401() throws Exception {
            final var loginDto = new LoginDto("user1", "user");
            final var requestBuilder =
                    MockMvcRequestBuilders.post(AUTH_URL + "/login")
                            .content(asJsonString(loginDto))
                            .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errors[0]", containsString("Bad credentials")))
                    .andExpect(cookie().doesNotExist(jwtCookieName));
        }

        @Test
        void login_bad_password_401() throws Exception {
            final var loginDto = new LoginDto("user", "user1");
            final var requestBuilder =
                    MockMvcRequestBuilders.post(AUTH_URL + "/login")
                            .content(asJsonString(loginDto))
                            .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errors[0]", containsString("Bad credentials")))
                    .andExpect(cookie().doesNotExist(jwtCookieName));
        }
    }

    @Nested
    @DisplayName("Logout")
    class Logout {

        @Test
        @WithMockUser(username = "user")
        void logout_200() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.get(AUTH_URL + "/logout");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(cookie().value(jwtCookieName, is(jwtPrefix)))
                    .andExpect(cookie().maxAge(jwtCookieName, 0));
        }

        @Test
        void logout_while_not_logged_in_401() throws Exception {
            final var requestBuilder = MockMvcRequestBuilders.get(AUTH_URL + "/logout");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(cookie().doesNotExist(jwtCookieName));
        }
    }
}
