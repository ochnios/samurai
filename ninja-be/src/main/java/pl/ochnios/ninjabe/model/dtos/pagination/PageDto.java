package pl.ochnios.ninjabe.model.dtos.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "${docs.dto.page}")
public class PageDto<T> {

    @Schema(description = "${docs.dto.page.items}")
    private final List<T> items;

    @Schema(description = "${docs.dto.page.pageNumber}")
    private final int pageNumber;

    @Schema(description = "${docs.dto.page.totalElements}")
    private final long totalElements;

    @Schema(description = "${docs.dto.page.totalPages}")
    private final int totalPages;
}
