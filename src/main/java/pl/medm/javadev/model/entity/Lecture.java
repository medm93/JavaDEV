package pl.medm.javadev.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lecture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String lecturer;
    private boolean completed;
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            mappedBy = "lectures"
    )
    private List<User> users = new ArrayList<>();

    public Lecture(Long id, String title, String description, String lecturer, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.lecturer = lecturer;
        this.completed = completed;
    }
}