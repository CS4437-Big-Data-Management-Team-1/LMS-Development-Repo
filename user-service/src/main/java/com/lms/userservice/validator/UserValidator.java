package com.lms.userservice.validator;

import com.lms.userservice.registration.UserRegistrationDTO;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Validator for user registration data.
 *
 * Contains validation logic for username, email, and password.
 * It ensures that the provided user registration details meet specific criteria
 *
 * @author Olan Healy
 */

@Component
public class UserValidator {

    private static final String PATTERN_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$"; // https://www.baeldung.com/java-regex-password-validation

    /**
     * Validates the user registration details.
     *
     * @param userDTO the user registration DTO containing the user details.
     * @throws IllegalArgumentException if any of the validation rules are violated.
     */
    public void validate(UserRegistrationDTO userDTO) {

        // Validate username (Has to be entered and > 4 characters)
        if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (userDTO.getUsername().length() <= 4) {
            throw new IllegalArgumentException("Username must be longer than 4 characters");
        }

        // Validate email
        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        // TODO Regex for email nd tha

        // Validate password
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        // Use method below
        if (!isValidPassword(userDTO.getPassword())) {
            throw new IllegalArgumentException("Password must have at least 1 lowercase letter, 1 uppercase letter, 1 digit, 1 special character, and be at least 8 characters long");
        }
    }

    /**
     * Checks if the password matches the regex pattern.
     *
     * @param password the password string to check.
     * @return true if the password matches the complexity requirements, false otherwise.
     */
    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(PATTERN_PASSWORD);
        return pattern.matcher(password).matches();
    }
}