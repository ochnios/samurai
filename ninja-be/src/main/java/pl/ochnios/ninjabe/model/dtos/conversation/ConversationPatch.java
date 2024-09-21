package pl.ochnios.ninjabe.model.dtos.conversation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import pl.ochnios.ninjabe.model.dtos.PatchDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversationPatch implements PatchDto {

    @NotBlank(message = "must not be blank or null")
    @Size(min = 3, message = "must have at least 3 characters")
    @Size(max = 32, message = "must have at most 32 characters")
    private String summary;
}
