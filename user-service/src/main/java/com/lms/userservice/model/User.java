package com.lms.userservice.model;


import jakarta.persistence.*;
import java.time.LocalDateTime;


/**
 * Represents a user entity mapped to the "users" table in the database (could be changed and only done locally AOK.
 * Contains fields for user ID, username, email, password hash, creation timestamp, last login, and balance.
 * The {@code createdAt} field is automatically set on creation.
 * Passwords are TODO be a hash,
 * basic getters and setters are provided for managing user data.
 *
 * @author Olan Healy
 */

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id",nullable = false, unique = true)
    private String id;

    private String username;
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(nullable = false)
    private Double balance = 0.0;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    // TODO PASSWORD HASH
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public String toString(){
        String s = "Username:" + getUsername() + "\nPassword: " + getPasswordHash() + "\nEmail: " + getEmail() + "\nCreated At: " + getCreatedAt() + "\nLast Login : " + getLastLogin() + "\nBalance: " + getBalance();
        return s;
    }
}