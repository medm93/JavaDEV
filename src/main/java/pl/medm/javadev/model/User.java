package pl.medm.javadev.model;

import pl.medm.javadev.constraint.Password;
import pl.medm.javadev.constraint.group.UserData;
import pl.medm.javadev.constraint.group.UserPassword;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotEmpty(
            message = "pl.medm.javadev.model.User.firstName.NotEmpty",
            groups = {UserData.class}
    )
    private String firstName;
    @NotEmpty(
            message = "pl.medm.javadev.model.User.lastName.NotEmpty",
            groups = {UserData.class}
    )
    private String lastName;
    @Email(
            message = "pl.medm.javadev.model.User.email.Email",
            groups = {UserData.class}
    )
    @NotEmpty(
            message = "pl.medm.javadev.model.User.email.NotEmpty",
            groups = {UserData.class}
    )
    private String email;
    @Password(
            message = "pl.medm.javadev.model.User.password.Password",
            groups = UserPassword.class
    )
    private String password;
    private String yearOfStudy;
    private String fieldOfStudy;
    private String indexNumber;
    @ManyToMany(mappedBy = "users")
    private List<Lecture> lectures = new ArrayList<>();

    public User() {
    }

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String password, String yearOfStudy, String fieldOfStudy, String indexNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.yearOfStudy = yearOfStudy;
        this.fieldOfStudy = fieldOfStudy;
        this.indexNumber = indexNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(String yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public List<Lecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<Lecture> lectures) {
        this.lectures = lectures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", yearOfStudy='" + yearOfStudy + '\'' +
                ", fieldOfStudy='" + fieldOfStudy + '\'' +
                ", indexNumber='" + indexNumber + '\'' +
                ", lectures=" + lectures +
                '}';
    }
}