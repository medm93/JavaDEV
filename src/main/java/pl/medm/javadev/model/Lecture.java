package pl.medm.javadev.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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
}