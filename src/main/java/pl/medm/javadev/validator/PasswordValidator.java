package pl.medm.javadev.validator;

import pl.medm.javadev.constraint.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private static final String PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-./:;<=>?@\\^_`{|}~])(?=\\S+$).{min,max}$";
    private static final String MIN = "min";
    private static final String MAX = "max";
    private String min;
    private String max;

    @Override
    public void initialize(Password constraintAnnotation) {
        this.min = "" + constraintAnnotation.min();
        this.max = "" + constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        boolean valid = true;
        if (notNullFilter(password)) {
            return false;
        } else {
            valid &= notEmptyFilter(password);
            valid &= patternFilter(password);
        }
        return valid;
    }

    private boolean notEmptyFilter(String password) {
        return !password.isEmpty();
    }

    private boolean notNullFilter(String password) {
        return password == null;
    }

    private boolean patternFilter(String password) {
        return password.matches(PATTERN.replace(MIN, min).replace(MAX, max));
    }
}
