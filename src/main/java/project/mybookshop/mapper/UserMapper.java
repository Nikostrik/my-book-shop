package project.mybookshop.mapper;

import org.mapstruct.Mapper;
import project.mybookshop.config.MapperConfig;
import project.mybookshop.dto.user.UserResponseDto;
import project.mybookshop.model.User;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponse(User savedUser);

}
