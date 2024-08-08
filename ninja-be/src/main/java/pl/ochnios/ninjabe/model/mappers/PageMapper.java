package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

import pl.ochnios.ninjabe.model.dtos.PageDto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PageMapper {

    default <T, DTO> PageDto<DTO> map(Page<T> page, Function<T, DTO> mapper) {
        List<DTO> dtos = page.getContent().stream().map(mapper).collect(Collectors.toList());

        PageDto<DTO> pageDto = new PageDto<>();
        pageDto.setItems(dtos);
        pageDto.setPageNumber(page.getNumber());
        pageDto.setTotalElements(Math.toIntExact(page.getTotalElements()));
        pageDto.setTotalPages(page.getTotalPages());

        return pageDto;
    }
}
