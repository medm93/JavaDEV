package pl.medm.javadev.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@Data
@JsonInclude(Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String yearOfStudy;
    private String fieldOfStudy;
    private String indexNumber;
}