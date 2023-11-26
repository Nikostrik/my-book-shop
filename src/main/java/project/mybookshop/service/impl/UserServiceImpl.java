package project.mybookshop.service.impl;

import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import project.mybookshop.dto.user.UserRegistrationRequestDto;
import project.mybookshop.dto.user.UserResponseDto;
import project.mybookshop.exceptions.EntityNotFoundException;
import project.mybookshop.exceptions.RegistrationException;
import project.mybookshop.mapper.UserMapper;
import project.mybookshop.model.Role;
import project.mybookshop.model.User;
import project.mybookshop.repository.role.RoleRepository;
import project.mybookshop.repository.user.UserRepository;
import project.mybookshop.service.UserService;

@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto request)
            throws RegistrationException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RegistrationException("Unable to complete registration.");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setShippingAddress(request.getShippingAddress());
        user.setRoles(Set.of(getUserRole(Role.RoleName.USER)));
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @Override
    public UserResponseDto updateUserRole(String email, Role.RoleName name) {
        Role userRole = getUserRole(name);
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            user.getRoles().add(userRole);
            user.setRoles(user.getRoles());
            return userMapper.toUserResponse(userRepository.save(user));
        }
        throw new EntityNotFoundException("Can't find user by email: " + email);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Can't find user by email: " + email));
    }

    private Role getUserRole(Role.RoleName name) {
        return roleRepository.getRoleByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    return roleRepository.save(role);
                });
    }
}
