package pl.ochnios.samurai.model.dtos.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "${docs.dto.page-request}")
public class PageRequestDto {

    @Schema(description = "${docs.dto.page-request.page}", defaultValue = "0", type = "number")
    private final Integer page;

    @Schema(description = "${docs.dto.page-request.size}", defaultValue = "10", type = "number")
    private final Integer size;

    @Schema(description = "${docs.dto.page-request.sortBy}", type = "array")
    private final List<String> sortBy;

    @Schema(
            description = "${docs.dto.page-request.sortDir}",
            type = "array",
            allowableValues = {"asc", "desc"})
    private final List<String> sortDir;
}
