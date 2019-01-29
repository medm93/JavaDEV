package pl.medm.javadev.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RoleExistsException extends RuntimeException {

    public RoleExistsException(String message) {
        super(message);
    }
}
