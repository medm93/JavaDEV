package pl.medm.javadev.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.medm.javadev.constraint.Password;
import pl.medm.javadev.constraint.group.UserData;
import pl.medm.javadev.constraint.group.UserPassword;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(message = "pl.medm.javadev.model.User.firstName.NotEmpty", groups = {UserData.class})
    private String firstName;
    @NotEmpty(message = "pl.medm.javadev.model.User.lastName.NotEmpty", groups = {UserData.class})
    private String lastName;
    @Email(message = "pl.medm.javadev.model.User.email.Email", groups = {UserData.class})
    @NotEmpty(message = "pl.medm.javadev.model.User.email.NotEmpty", groups = {UserData.class})
    private String email;
    @Password(message = "pl.medm.javadev.model.User.password.Password", groups = UserPassword.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String yearOfStudy;
    private String fieldOfStudy;
    private String indexNumber;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_lecture",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "lecture_id", referencedColumnName = "id")}
    )
    @JsonIgnore
    private List<Lecture> lectures = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<Role> roles = new HashSet<>();
}