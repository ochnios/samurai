package pl.ochnios.ninjabe.model.dtos;

import lombok.Data;

@Data
public class PageDto<T> {

    private Iterable<T> items;
    private int pageNumber;
    private int totalElements;
    private int totalPages;
}
