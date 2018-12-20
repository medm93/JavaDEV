package pl.medm.javadev.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class LectureIsEndException extends RuntimeException {
    public LectureIsEndException(String message) {
        super(message);
    }
}
