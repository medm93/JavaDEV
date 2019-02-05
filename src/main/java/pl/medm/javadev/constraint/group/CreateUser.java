package pl.medm.javadev.constraint.group;

import javax.validation.GroupSequence;

@GroupSequence({BasicData.class, PasswordData.class, StudiesData.class})
public interface CreateUser {
}
