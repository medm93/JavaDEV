package pl.medm.javadev.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Lecture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String lecturer;
    private Boolean completed;
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            mappedBy = "lectures"
    )
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        user.getLectures().add(this);
        users.add(user);
    }

    public Lecture(Long id, String title, String description, String lecturer, Boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lecturer = lecturer;
        this.completed = completed;
    }
}