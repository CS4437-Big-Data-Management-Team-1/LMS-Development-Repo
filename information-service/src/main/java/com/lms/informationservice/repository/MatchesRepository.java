package com.lms.informationservice.repository;

import com.lms.informationservice.matches.Matches;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * MatchesRepository interface for managing match entities in the database.
 *
 * This repository provides CRUD operations
 * and query methods for the {@link Matches} entity by extending the
 * {@link JpaRepository} interface.
 *
 * @author Caoimhe Cahill
 */
@Repository
public interface MatchesRepository extends JpaRepository<Matches, Integer> {
}
