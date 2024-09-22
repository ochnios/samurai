package pl.ochnios.ninjabe.model.dtos.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRequestDto {

    private final UUID conversationId;

    @NotBlank(message = "must not be blank or null")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 8192, message = "must have at most 8192 characters")
    private final String question;
}
