package pl.ochnios.samurai.model.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.ochnios.samurai.model.dtos.pagination.PageDto;
import pl.ochnios.samurai.model.dtos.pagination.PageRequestDto;

@Mapper
public interface PageMapper {

    int MIN_PAGE_NUMBER = 0;
    int DEFAULT_PAGE_NUMBER = 0;
    int MAX_PAGE_SIZE = 1000;
    int DEFAULT_PAGE_SIZE = 10;
    Sort DEFAULT_SORT = Sort.unsorted();

    default <T, DTO> PageDto<DTO> validOrDefaultSort(Page<T> page, Function<T, DTO> mapper) {
        if (page == null) {
            return new PageDto<>(List.of(), 0, 0, 0);
        } else {
            var dtos = page.getContent().stream().map(mapper).collect(Collectors.toList());
            return new PageDto<>(dtos, page.getNumber(), page.getTotalElements(), page.getTotalPages());
        }
    }

    default PageRequest validOrDefaultSort(PageRequestDto pageRequestDto) {
        if (pageRequestDto == null) {
            return PageRequest.of(0, 10);
        }
        var page = validOrDefaultPage(pageRequestDto.getPage());
        var size = validOrDefaultSize(pageRequestDto.getSize());
        var sort = validOrDefaultSort(pageRequestDto.getSortBy(), pageRequestDto.getSortDir());
        return PageRequest.of(page, size, sort);
    }

    default Integer validOrDefaultPage(Integer page) {
        if (page == null || page < MIN_PAGE_NUMBER) {
            return 0;
        } else {
            return page;
        }
    }

    default Integer validOrDefaultSize(Integer size) {
        if (size == null || size < DEFAULT_PAGE_NUMBER || size > MAX_PAGE_SIZE) {
            return DEFAULT_PAGE_SIZE;
        } else {
            return size;
        }
    }

    default Sort validOrDefaultSort(List<String> sortBy, List<String> sortDir) {
        if (sortBy != null) {
            List<Sort.Order> orders = new ArrayList<>();
            for (int i = 0; i < sortBy.size(); i++) {
                if (sortDir != null && sortDir.size() > i) {
                    var order = "asc".equalsIgnoreCase(sortDir.get(i))
                            ? Sort.Order.asc(sortBy.get(i))
                            : Sort.Order.desc(sortBy.get(i));
                    orders.add(order);
                } else {
                    orders.add(Sort.Order.asc(sortBy.get(i)));
                }
            }
            return Sort.by(orders);
        } else {
            return DEFAULT_SORT;
        }
    }
}
