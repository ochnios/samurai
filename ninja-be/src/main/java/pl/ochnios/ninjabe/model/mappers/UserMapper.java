package pl.ochnios.ninjabe.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.ninjabe.model.dtos.user.UserDto;
import pl.ochnios.ninjabe.model.entities.user.User;

@Mapper
public interface UserMapper {

    UserDto map(User user);
}
