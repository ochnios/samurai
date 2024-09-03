package pl.ochnios.ninjabe.model.dtos.pagination;

import lombok.Data;

@Data
public class PageDto<T> {

    private final Iterable<T> items;
    private final int pageNumber;
    private final long totalElements;
    private final int totalPages;
}
