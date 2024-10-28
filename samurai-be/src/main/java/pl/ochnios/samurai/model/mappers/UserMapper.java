package pl.ochnios.samurai.model.mappers;

import org.mapstruct.Mapper;
import pl.ochnios.samurai.model.dtos.user.UserDto;
import pl.ochnios.samurai.model.entities.user.User;

@Mapper
public interface UserMapper {

    UserDto map(User user);
}
