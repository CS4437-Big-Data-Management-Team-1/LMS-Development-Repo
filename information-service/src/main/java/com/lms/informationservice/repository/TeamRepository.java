package com.lms.informationservice.repository;

import com.lms.informationservice.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TeamRepository interface for managing match entities in the database.
 *
 * This repository provides CRUD operations
 * and query methods for the {@link Team} entity by extending the
 * {@link JpaRepository} interface.
 *
 * @author Caoimhe Cahill
 */
public interface TeamRepository extends JpaRepository<Team, Integer> {
}
