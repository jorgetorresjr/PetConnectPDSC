package com.PetConnect.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.Period;
import java.util.Date;

public class AdulthoodValidator implements ConstraintValidator<Adulthood, Date> {

    @Override
    public boolean isValid(Date value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

         LocalDate nascimento = new java.sql.Date(value.getTime())
                .toLocalDate();

        return Period.between(nascimento, LocalDate.now()).getYears() >= 18 && Period.between(nascimento, LocalDate.now()).getYears() < 110;
    }
}