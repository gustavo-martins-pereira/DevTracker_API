package com.gustavomp.devtrackerapi.validators;

import com.gustavomp.devtrackerapi.annotations.ValidProjectType;
import com.gustavomp.devtrackerapi.dtos.requests.project.CreateProjectRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class ValidProjectTypeValidator implements ConstraintValidator<ValidProjectType, CreateProjectRequestDto> {

    @Override
    public boolean isValid(CreateProjectRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.projectType() == null) {
            return false;
        }

        context.disableDefaultConstraintViolation();

        if ("FIXED_PRICE".equalsIgnoreCase(dto.projectType())) {
            if (dto.budget() == null || dto.budget().compareTo(BigDecimal.ZERO) <= 0) {
                context.buildConstraintViolationWithTemplate(
                        "Budget is required and must be greater than 0 for FIXED_PRICE projects"
                        )
                        .addPropertyNode("budget")
                        .addConstraintViolation();

                return false;
            }

            return true;
        }

        if ("HOURLY_RATE".equalsIgnoreCase(dto.projectType())) {

            if (dto.hourlyRate() == null
                    || dto.hourlyRate().compareTo(BigDecimal.ZERO) <= 0) {

                context.buildConstraintViolationWithTemplate(
                                "Hourly rate is required and must be greater than 0 for HOURLY_RATE projects"
                        )
                        .addPropertyNode("hourlyRate")
                        .addConstraintViolation();

                return false;
            }

            return true;
        }

        // Invalid project type
        context.buildConstraintViolationWithTemplate("Invalid project type '" + dto.projectType()
                        + "'. Available options: [FIXED_PRICE, HOURLY_RATE]"
                )
                .addPropertyNode("projectType")
                .addConstraintViolation();

        return false;
    }
}