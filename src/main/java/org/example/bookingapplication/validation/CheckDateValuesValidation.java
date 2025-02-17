package org.example.bookingapplication.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class CheckDateValuesValidation
        implements ConstraintValidator<CheckDateValues, LocalDate[]> {
    private static final int CHECK_IN_DATE_POSITION = 0;
    private static final int CHECK_OUT_DATE_POSITION = 1;
    private static final int DATES_ARR_LENGTH = 2;

    @Override
    public boolean isValid(LocalDate[] checkDates,
                           ConstraintValidatorContext constraintValidatorContext) {
        if (checkDates == null) {
            return true;
        }
        if (checkDates.length != DATES_ARR_LENGTH) {
            return false;
        }

        LocalDate checkInDate = checkDates[CHECK_IN_DATE_POSITION];
        LocalDate checkOutDate = checkDates[CHECK_OUT_DATE_POSITION];

        LocalDate today = LocalDate.now();
        if (checkInDate.isBefore(today)) {
            return false;
        }

        if (checkInDate.isEqual(checkOutDate)) {
            return false;
        }

        if (checkInDate.isAfter(checkOutDate)) {
            return false;
        }

        return true;
    }
}
