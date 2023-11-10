package project.mybookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mybookshop.dto.user.UserLoginRequestDto;
import project.mybookshop.dto.user.UserLoginResponseDto;
import project.mybookshop.dto.user.UserRegistrationRequestDto;
import project.mybookshop.dto.user.UserResponseDto;
import project.mybookshop.exceptions.RegistrationException;
import project.mybookshop.security.AuthenticationService;
import project.mybookshop.service.UserService;

@Tag(name = "Authentication management", description = "Endpoints for authentication users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "Login a user", description = "Receiving a token after user login")
    @PostMapping("/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return authenticationService.authenticate(request);
    }

    @Operation(summary = "Register a user", description = "Registration of a new user")
    @PostMapping("/register")
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto request)
            throws RegistrationException {
        return userService.register(request);
    }
}
