package pl.medm.javadev.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LectureDTO {

    private Long id;
    private String title;
    private String description;
    private String lecturer;
}
