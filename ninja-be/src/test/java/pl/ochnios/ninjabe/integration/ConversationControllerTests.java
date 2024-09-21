package pl.ochnios.ninjabe.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import pl.ochnios.ninjabe.model.seeders.ConversationSeeder;
import pl.ochnios.ninjabe.model.seeders.UserSeeder;
import pl.ochnios.ninjabe.repositories.impl.ConversationCrudRepository;
import pl.ochnios.ninjabe.repositories.impl.MessageCrudRepository;
import pl.ochnios.ninjabe.repositories.impl.UserCrudRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ConversationControllerTests {

    private static final String CONVERSATIONS_URI = "/conversations";

    @Autowired private MockMvc mockMvc;
    @Autowired private UserSeeder userSeeder;
    @Autowired private ConversationSeeder conversationSeeder;
    @Autowired private UserCrudRepository userCrudRepository;
    @Autowired private ConversationCrudRepository conversationCrudRepository;
    @Autowired private MessageCrudRepository messageCrudRepository;

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
}
