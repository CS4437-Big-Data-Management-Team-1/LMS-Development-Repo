package com.lms.userservice.registration;

/**
 * Data Transfer Object (DTO) for user registration.
 *
 * This class is used to transfer user registration data such as username, email, and password
 * between the client and the server. It encapsulates the registration details provided by the user.
 *
 *
 * @author Olan Healy
 */
public class UserRegistrationDTO {
    private String username;
    private String email;
    private String password;


    /**
     * Gets username
     *
     * @return the username entered by the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets email address.
     *
     * @return the email address entered by the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email address
     *
     *
     * @param email set provided by user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets password
     *
     * @return the password entered by the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password
     *
     * @param password set by user
     */
    public void setPassword(String password) {
        this.password = password;
    }
}