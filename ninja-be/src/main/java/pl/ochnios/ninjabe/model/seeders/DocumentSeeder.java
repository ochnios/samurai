package pl.ochnios.ninjabe.model.seeders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import pl.ochnios.ninjabe.commons.exceptions.ApplicationException;
import pl.ochnios.ninjabe.model.entities.document.DocumentEntity;
import pl.ochnios.ninjabe.repositories.DocumentRepository;
import pl.ochnios.ninjabe.repositories.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

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
        final var user = userRepository.findByUsername(username);
        final var file = getFile(filepath);
        final var document = DocumentEntity.builder()
                .id(UUID.nameUUIDFromBytes(title.getBytes()))
                .file(file)
                .uploader(user)
                .title(title)
                .description(title + " - " + "description")
                .build();
        final var savedDocument = documentRepository.save(document);
        log.info("Created document: {}", savedDocument);
    }

    private File getFile(String filepath) {
        final var resource = new ClassPathResource(filepath);
        try {
            return resource.getFile();
        } catch (IOException ex) {
            throw new ApplicationException("Failed to read file", ex);
        }
    }
}
