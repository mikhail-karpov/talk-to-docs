package com.mikhailkarpov.docs.documents.web;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FileValidator.class)
public @interface FileConstraint {

  String message() default "Illegal file";

  Class<?>[] groups() default { };

  Class<? extends Payload>[] payload() default { };

  String[] allowedTypes() default { };
}
