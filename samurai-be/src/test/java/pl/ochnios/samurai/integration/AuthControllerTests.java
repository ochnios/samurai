package pl.ochnios.samurai.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.ochnios.samurai.TestUtils.asJsonString;

import org.junit.jupiter.api.AfterAll;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.ochnios.samurai.model.dtos.auth.LoginDto;
import pl.ochnios.samurai.model.dtos.auth.RegisterDto;
import pl.ochnios.samurai.model.entities.user.Role;
import pl.ochnios.samurai.model.seeders.UserSeeder;
import pl.ochnios.samurai.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"local", "test"})
public class AuthControllerTests {

    private static final String AUTH_URL = "/auth";

    @Value("${custom.jwt.cookie.name}")
    private String jwtCookieName;

    @Value("${custom.jwt.cookie.prefix}")
    private String jwtPrefix;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Autowired
    private UserSeeder userSeeder;

    @BeforeAll
    public void setup() {
        userCrudRepository.deleteAll();
        userSeeder.seed();
    }

    @AfterAll
    public void tearDown() {
        userCrudRepository.deleteAll();
    }

    @Nested
    @DisplayName("Login")
    class Login {

        @Test
        void login_as_user_200() throws Exception {
            var loginDto = new LoginDto("user", "user");
            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/login")
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
            var loginDto = new LoginDto("mod", "mod");
            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/login")
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
            var loginDto = new LoginDto("admin", "admin");
            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/login")
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
            var loginDto = new LoginDto("user1", "user");
            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/login")
                    .content(asJsonString(loginDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.errors[0]", containsString("Bad credentials")))
                    .andExpect(cookie().doesNotExist(jwtCookieName));
        }

        @Test
        void login_bad_password_401() throws Exception {
            var loginDto = new LoginDto("user", "user1");
            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/login")
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
            var requestBuilder = MockMvcRequestBuilders.get(AUTH_URL + "/logout");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(cookie().value(jwtCookieName, is(jwtPrefix)))
                    .andExpect(cookie().maxAge(jwtCookieName, 0));
        }

        @Test
        void logout_while_not_logged_in_401() throws Exception {
            var requestBuilder = MockMvcRequestBuilders.get(AUTH_URL + "/logout");
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isUnauthorized())
                    .andExpect(cookie().doesNotExist(jwtCookieName));
        }
    }

    @Nested
    @DisplayName("Register")
    class Register {

        @Test
        void register_valid_user_200() throws Exception {
            var registerDto =
                    new RegisterDto("newuser", "John", "Doe", "newuser@users.com", "Password1!", "Password1!");

            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/register")
                    .content(asJsonString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("newuser")))
                    .andExpect(jsonPath("$.firstname", is("John")))
                    .andExpect(jsonPath("$.lastname", is("Doe")))
                    .andExpect(jsonPath("$.email", is("newuser@users.com")))
                    .andExpect(jsonPath("$.role", is(Role.User.name())));
        }

        @Test
        void register_existing_username_400() throws Exception {
            var registerDto = new RegisterDto(
                    "user", // existing username from UserSeeder
                    "John",
                    "Doe",
                    "another@users.com",
                    "Password1!",
                    "Password1!");

            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/register")
                    .content(asJsonString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("Username already exists")));
        }

        @Test
        void register_existing_email_400() throws Exception {
            var registerDto = new RegisterDto(
                    "newuser",
                    "John",
                    "Doe",
                    "user@users.com", // existing email from UserSeeder
                    "Password1!",
                    "Password1!");

            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/register")
                    .content(asJsonString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("Email already exists")));
        }

        @Test
        void register_passwords_not_matching_400() throws Exception {
            var registerDto =
                    new RegisterDto("newuser", "John", "Doe", "newuser@users.com", "Password1!", "DifferentPassword1!");

            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/register")
                    .content(asJsonString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("Passwords do not match")));
        }

        @Test
        void register_invalid_username_format_400() throws Exception {
            var registerDto = new RegisterDto(
                    "u$", // invalid username format
                    "John",
                    "Doe",
                    "newuser@users.com",
                    "Password1!",
                    "Password1!");

            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/register")
                    .content(asJsonString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("must be between 3 and 30 characters")));
        }

        @Test
        void register_invalid_password_format_400() throws Exception {
            var registerDto = new RegisterDto(
                    "newuser",
                    "John",
                    "Doe",
                    "newuser@users.com",
                    "weak", // invalid password format
                    "weak");

            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/register")
                    .content(asJsonString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("must be 8-30 characters long")));
        }

        @Test
        void register_invalid_email_format_400() throws Exception {
            var registerDto = new RegisterDto(
                    "newuser",
                    "John",
                    "Doe",
                    "invalid-email", // invalid email format
                    "Password1!",
                    "Password1!");

            var requestBuilder = MockMvcRequestBuilders.post(AUTH_URL + "/register")
                    .content(asJsonString(registerDto))
                    .contentType(MediaType.APPLICATION_JSON);

            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors[0]", containsString("must be a correct email")));
        }
    }
}
