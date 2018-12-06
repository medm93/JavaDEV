package pl.medm.javadev.constraint.group;

import javax.validation.GroupSequence;

@GroupSequence({UserData.class, UserPassword.class})
public interface UserRegistration {
}
