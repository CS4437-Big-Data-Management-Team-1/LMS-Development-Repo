package com.lms.userservice.login;

/**
 * Data Transfer Object (DTO) for user login
 *
 * This class is used to transfer login data (email and password)
 * between the client and the server during the login process.
 *
 * @author Olan Healy
 */

public class UserLoginDTO {

    private String email;
    private String password;

    /**
     * Gets email address
     *
     * @return email entered
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email address
     *
     * @param email set provided by the user during login
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets password.
     *
     * @return password entered by the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password
     *
     * @param password provided by the user during login
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
