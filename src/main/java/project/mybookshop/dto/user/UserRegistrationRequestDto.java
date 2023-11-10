package project.mybookshop.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import project.mybookshop.validation.Email;
import project.mybookshop.validation.FieldMatch;


@FieldMatch(field = "password",
        fieldMatch = "repeatPassword",
        message = "Password fields must match")
@Data
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    @Size(min = 8, max = 50)
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    @NotBlank
    @Size(min = 8, max = 100)
    private String repeatPassword;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String shippingAddress;
}
