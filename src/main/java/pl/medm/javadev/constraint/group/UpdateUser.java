package pl.medm.javadev.constraint.group;

import javax.validation.GroupSequence;

@GroupSequence({BasicData.class, StudiesData.class})
public interface UpdateUser {
}
