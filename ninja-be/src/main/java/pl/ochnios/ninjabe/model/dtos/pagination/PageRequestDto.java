package pl.ochnios.ninjabe.model.dtos.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
@Schema(description = "${docs.dto.page-request}")
public class PageRequestDto {

    @Schema(description = "${docs.dto.page-request.page}", defaultValue = "0")
    private final Integer page;

    @Schema(description = "${docs.dto.page-request.size}", defaultValue = "10")
    private final Integer size;

    @Schema(description = "${docs.dto.page-request.sortBy}")
    private final String sortBy;

    @Schema(description = "${docs.dto.page-request.sortDir}")
    private final Sort.Direction sortDir;

    public PageRequestDto(Integer page, Integer size, String sortBy, Sort.Direction sortDir) {
        this.page = validOrDefaultPage(page);
        this.size = validOrDefaultSize(size);
        this.sortBy = validOrDefaultSortBy(sortBy);
        this.sortDir = sortDir;
    }

    private Integer validOrDefaultPage(Integer page) {
        if (page == null || page < 0) {
            return 0;
        } else {
            return page;
        }
    }

    private Integer validOrDefaultSize(Integer size) {
        if (size == null || size < 1 || size > 1000) {
            return 10;
        } else {
            return size;
        }
    }

    private String validOrDefaultSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return null;
        } else {
            return sortBy;
        }
    }
}
