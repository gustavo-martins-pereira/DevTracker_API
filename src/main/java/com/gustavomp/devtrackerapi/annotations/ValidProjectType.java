package com.gustavomp.devtrackerapi.annotations;

import com.gustavomp.devtrackerapi.validators.ValidProjectTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidProjectTypeValidator.class)
public @interface ValidProjectType {
    String message() default "Invalid monetary field for the selected project type. Available Options: [FIXED_PRICE, HOURLY_RATE]";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
