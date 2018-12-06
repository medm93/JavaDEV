package pl.medm.javadev.constraint;

import pl.medm.javadev.validator.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ METHOD, FIELD, CONSTRUCTOR, PARAMETER, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface Password {
    String message() default "Bad password format!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default 6;
    int max() default 20;
}
