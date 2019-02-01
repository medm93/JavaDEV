package pl.medm.javadev.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureDTO {

    private Long id;
    private String title;
    private String description;
    private String lecturer;
    private boolean completed;

    public LectureDTO(String title, String description, String lecturer, boolean completed) {
        this.title = title;
        this.description = description;
        this.lecturer = lecturer;
        this.completed = completed;
    }
}
