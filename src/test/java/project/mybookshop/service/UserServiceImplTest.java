package project.mybookshop.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import project.mybookshop.dto.user.UserRegistrationRequestDto;
import project.mybookshop.dto.user.UserResponseDto;
import project.mybookshop.mapper.UserMapper;
import project.mybookshop.model.Role;
import project.mybookshop.model.User;
import project.mybookshop.repository.role.RoleRepository;
import project.mybookshop.repository.user.UserRepository;
import project.mybookshop.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final Long TEST_ID = 1L;
    private static final String TEST_EMAIL = "test@gmail.com";
    private static final String TEST_PASSWORD = "1234";
    private static User user;
    private static UserRegistrationRequestDto requestDto;
    private static UserResponseDto responseDto;
    private static Role role;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeAll
    static void beforeAll() {
        user = new User();
        requestDto = new UserRegistrationRequestDto();
        responseDto = new UserResponseDto();
        role = new Role();
    }

    @BeforeEach
    void setup() {
        user.setId(TEST_ID)
                .setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD);
        requestDto.setEmail(TEST_EMAIL)
                .setPassword(TEST_PASSWORD)
                .setRepeatPassword(TEST_PASSWORD);
        responseDto.setId(TEST_ID)
                .setEmail(TEST_EMAIL);
        role.setName(Role.RoleName.USER);
    }

    @Test
    @DisplayName("Verify the user was registered correct")
    public void register_WithValidRegistrationReqDto_ReturnUserRespDto() {
        when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encoded password");
        when(roleRepository.getRoleByName(Role.RoleName.USER))
                .thenReturn(Optional.ofNullable(role));
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(responseDto);

        UserResponseDto actual = userService.register(requestDto);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(responseDto, actual);
    }

    @Test
    @DisplayName("Find User By Email")
    public void findUserByEmail_WithExistingEmail_ReturnUser() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(user));

        User actual = userService.findUserByEmail(TEST_EMAIL);

        Assertions.assertEquals(user, actual);
    }
}
