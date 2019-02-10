package pl.medm.javadev.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureDTO {

    private Long id;
    @NotEmpty
    @Size(min = 5, max = 100)
    private String title;
    @Size(max = 512)
    private String description;
    @NotEmpty
    @Size(min = 5, max = 40)
    private String lecturer;
    private boolean completed;

    public LectureDTO(String title, String description, String lecturer, boolean completed) {
        this.title = title;
        this.description = description;
        this.lecturer = lecturer;
        this.completed = completed;
    }
}
