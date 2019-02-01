package pl.medm.javadev.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class LectureConflictException extends RuntimeException {
    public LectureConflictException(String message) {
        super(message);
    }
}
