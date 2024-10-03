package pl.ochnios.ninjabe.model.mappers;

import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pl.ochnios.ninjabe.model.dtos.pagination.PageDto;
import pl.ochnios.ninjabe.model.dtos.pagination.PageRequestDto;

@Mapper(componentModel = "spring")
public interface PageMapper {

    default <T, DTO> PageDto<DTO> map(Page<T> page, Function<T, DTO> mapper) {
        final var dtos = page.getContent().stream().map(mapper).collect(Collectors.toList());
        return new PageDto<>(dtos, page.getNumber(), page.getTotalElements(), page.getTotalPages());
    }

    default PageRequest map(PageRequestDto pageRequestDto) {
        if (pageRequestDto == null) {
            return PageRequest.of(0, 10);
        }
        final var sort = map(pageRequestDto.getSortBy(), pageRequestDto.getSortDir());
        return PageRequest.of(pageRequestDto.getPage(), pageRequestDto.getSize(), sort);
    }

    default Sort map(String sortBy, Sort.Direction sortDir) {
        if (sortBy != null && sortDir != null) {
            return Sort.by(sortDir, sortBy);
        } else {
            return Sort.unsorted();
        }
    }
}
