package pl.medm.javadev.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.medm.javadev.constraint.Password;
import pl.medm.javadev.constraint.group.BasicData;
import pl.medm.javadev.constraint.group.PasswordData;
import pl.medm.javadev.constraint.group.StudiesData;

import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    @NotEmpty(groups = BasicData.class)
    private String firstName;
    @NotEmpty(groups = BasicData.class)
    private String lastName;
    @Email(groups = BasicData.class)
    @NotEmpty(groups = BasicData.class)
    private String email;
    @Password(groups = PasswordData.class)
    private String password;
    @Size(min = 1, max = 1, groups = StudiesData.class)
    @Pattern(regexp = "^[0-5]+$", groups = StudiesData.class)
    private String yearOfStudy;
    @Size(min = 5, groups = StudiesData.class)
    @Pattern(regexp = "^[a-zA-Z]+$", groups = StudiesData.class)
    private String fieldOfStudy;
    @Size(min = 6, max = 6, groups = StudiesData.class)
    @Pattern(regexp = "^[0-9]+$", groups = StudiesData.class)
    private String indexNumber;
    @NotNull
    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO(String firstName, String lastName, String email, String password, String yearOfStudy, String fieldOfStudy, String indexNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.yearOfStudy = yearOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.indexNumber = indexNumber;
    }

    public UserDTO(Long id, String password) {
        this.id = id;
        this.password = password;
    }

    public UserDTO(Long id, String firstName, String lastName, String email, String password, String yearOfStudy,
                   String fieldOfStudy, String indexNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.yearOfStudy = yearOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.indexNumber = indexNumber;
    }
}