package pl.ochnios.ninjabe.model.dtos.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "${docs.dto.chat-request}")
public class ChatRequestDto {

    @Schema(description = "${docs.dto.chat-request.conversationId}")
    private final UUID conversationId;

    @Schema(description = "${docs.dto.chat-request.question}")
    @NotBlank(message = "must not be blank or null")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 8192, message = "must have at most 8192 characters")
    private final String question;
}
