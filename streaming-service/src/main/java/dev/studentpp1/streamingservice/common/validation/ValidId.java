package dev.studentpp1.streamingservice.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Positive;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Positive(message = "ID must be positive")
public @interface ValidId {

    String message() default "ID must be positive";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}