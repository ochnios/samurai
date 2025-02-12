package pl.ochnios.samurai.model.seeders;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import pl.ochnios.samurai.commons.exceptions.ApplicationException;
import pl.ochnios.samurai.model.entities.document.DocumentEntity;
import pl.ochnios.samurai.repositories.DocumentRepository;
import pl.ochnios.samurai.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Override
    public void seed() {
        createDocument("Sample PDF", "admin", "files/sample.pdf");
        createDocument("Sample DOCX", "mod", "files/sample.docx");
    }

    private void createDocument(String title, String username, String filepath) {
        var user = userRepository.findByUsername(username);
        var documentId = UUID.nameUUIDFromBytes(title.getBytes());
        if (!documentRepository.findAllById(List.of(documentId)).isEmpty()) {
            log.info("Document {} already exists, cancelling seeding", documentId);
            return;
        }

        var file = getFile(filepath);
        var document = DocumentEntity.builder()
                .id(documentId)
                .file(file)
                .user(user)
                .title(title)
                .description(title + " - " + "description")
                .build();
        var savedDocument = documentRepository.save(document);
        log.info("Created document: {}", savedDocument);
    }

    private File getFile(String filepath) {
        var resource = new ClassPathResource(filepath);
        try {
            return resource.getFile();
        } catch (IOException ex) {
            throw new ApplicationException("Failed to read file", ex);
        }
    }
}
