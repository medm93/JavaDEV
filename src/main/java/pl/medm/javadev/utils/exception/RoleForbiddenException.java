package pl.medm.javadev.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RoleForbiddenException extends RuntimeException {
    public RoleForbiddenException(String message) {
        super(message);
    }
}
