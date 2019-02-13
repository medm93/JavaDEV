package pl.medm.javadev.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    private String password;
    @Column(name = "year_of_study")
    private String yearOfStudy;
    @Column(name = "field_of_study")
    private String fieldOfStudy;
    @Column(name = "index_number")
    private String indexNumber;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_lecture",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "lecture_id", referencedColumnName = "id")}
    )
    private List<Lecture> lectures = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<Role> roles = new HashSet<>();

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

    public User(Long id, String firstName, String lastName, String email, String password, String yearOfStudy,
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