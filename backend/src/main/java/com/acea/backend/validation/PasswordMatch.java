package com.acea.backend.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.lang.reflect.Field;

/**
 * Cross-field validation annotation: ensures two password fields match.
 *
 * <p>Usage:
 * <pre>
 * {@literal @}PasswordMatch(password = "password", confirmPassword = "confirmPassword")
 * public record RegisterRequest(...) {}
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatch.PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch {

    String message() default "Passwords do not match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String password();
    String confirmPassword();

    class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

        private String passwordField;
        private String confirmPasswordField;

        @Override
        public void initialize(PasswordMatch annotation) {
            this.passwordField        = annotation.password();
            this.confirmPasswordField = annotation.confirmPassword();
        }

        @Override
        public boolean isValid(Object obj, ConstraintValidatorContext context) {
            try {
                // Works with both classes and records
                Object password = getFieldValue(obj, passwordField);
                Object confirm  = getFieldValue(obj, confirmPasswordField);

                boolean valid = password != null && password.equals(confirm);
                if (!valid) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate("Passwords do not match")
                           .addPropertyNode(confirmPasswordField)
                           .addConstraintViolation();
                }
                return valid;
            } catch (Exception e) {
                return false;
            }
        }

        private Object getFieldValue(Object obj, String fieldName) throws Exception {
            // Support Java records (accessor methods) and regular fields
            try {
                return obj.getClass().getMethod(fieldName).invoke(obj);
            } catch (NoSuchMethodException e) {
                Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(obj);
            }
        }
    }
}
