package com.PetConnect.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AdulthoodValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adulthood {

    String message() default "O usuário deve ser maior de 18 anos.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}