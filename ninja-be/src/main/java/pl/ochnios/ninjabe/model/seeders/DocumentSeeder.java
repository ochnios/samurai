package pl.ochnios.ninjabe.model.seeders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import pl.ochnios.ninjabe.model.entities.user.User;
import pl.ochnios.ninjabe.repositories.DocumentRepository;
import pl.ochnios.ninjabe.repositories.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentSeeder implements DataSeeder {

    private final UserRepository userRepository;
    private final DocumentRepository documentRepository;

    @Override
    public void seed() {
        final var mod = userRepository.findByUsername("mod");
        final var admin = userRepository.findByUsername("admin");
        // allow to create FileEntity not only from MultipartFile...
    }

    private void createDocument(User uploader, MultipartFile multipartFile) {
        log.info("Created document");
    }
}
