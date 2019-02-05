package pl.medm.javadev.model.entity;

import lombok.*;
import pl.medm.javadev.constraint.Password;
import pl.medm.javadev.constraint.group.BasicData;
import pl.medm.javadev.constraint.group.PasswordData;
import pl.medm.javadev.constraint.group.StudiesData;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(groups = BasicData.class)
    @Column(name = "first_name")
    private String firstName;
    @NotEmpty(groups = BasicData.class)
    @Column(name = "last_name")
    private String lastName;
    @Email(groups = BasicData.class)
    @NotEmpty(groups = BasicData.class)
    private String email;
    @Password(groups = PasswordData.class)
    private String password;
    @Size(min = 1, max = 1, groups = StudiesData.class)
    @Pattern(regexp = "^[0-5]+$", groups = StudiesData.class)
    @Column(name = "year_of_study")
    private String yearOfStudy;
    @Size(min = 5, groups = StudiesData.class)
    @Pattern(regexp = "^[a-zA-Z]+$", groups = StudiesData.class)
    @Column(name = "field_of_study")
    private String fieldOfStudy;
    @Size(min = 6, max = 6, groups = StudiesData.class)
    @Pattern(regexp = "^[0-9]+$", groups = StudiesData.class)
    @Column(name = "index_number")
    private String indexNumber;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_lecture",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "lecture_id", referencedColumnName = "id")}
    )
    private List<Lecture> lectures = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<Role> roles = new HashSet<>();

    public User(Long id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String password, String yearOfStudy,
                String fieldOfStudy, String indexNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.yearOfStudy = yearOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.indexNumber = indexNumber;
    }

    public User(long id, String firstName, String lastName, String email, String password, String yearOfStudy,
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