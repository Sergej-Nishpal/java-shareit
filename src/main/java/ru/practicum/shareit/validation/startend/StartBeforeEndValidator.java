package ru.practicum.shareit.validation.startend;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEndValid, StartEndDated> {
    @Override
    public void initialize(StartBeforeEndValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(StartEndDated startEndDated, ConstraintValidatorContext constraintValidatorContext) {
        final LocalDateTime start = startEndDated.getStart();
        final LocalDateTime end = startEndDated.getEnd();

        return !start.isAfter(end);
    }
}
