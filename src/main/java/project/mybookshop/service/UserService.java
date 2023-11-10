package project.mybookshop.service;

import project.mybookshop.dto.user.UserRegistrationRequestDto;
import project.mybookshop.dto.user.UserResponseDto;
import project.mybookshop.exceptions.RegistrationException;
import project.mybookshop.model.Role;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto request) throws RegistrationException;

    UserResponseDto updateUserRole(String email, Role.RoleName name);
}
