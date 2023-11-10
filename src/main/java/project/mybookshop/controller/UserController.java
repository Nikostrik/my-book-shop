package project.mybookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mybookshop.dto.user.UserResponseDto;
import project.mybookshop.model.Role;
import project.mybookshop.service.UserService;

@Tag(name = "User management", description = "Endpoints for user management")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Add user role", description = "Add a new role to a user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    public UserResponseDto addUserRole(
            String email,
            Role.RoleName name) {
        return userService.updateUserRole(email, name);
    }
}
