package com.lms.userservice.repository;

import com.lms.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * UserRepository interface for managing user entities in the database.
 *
 * This repository provides CRUD operations
 * and query methods for the {@link User} entity by extending the
 * {@link JpaRepository} interface.
 * JpaRepository provides pre-defined methods which can be used to interact with the users
 * table in the database without writing custom SQL queries and saving time and complexity.
 *
 *
 * @author Olan Healy
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}