package com.lms.gameservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lms.gameservice.model.Results;

@Repository
public interface ResultsRepository extends JpaRepository<Results, Long>{
    
    @Query("SELECT r FROM Results r ORDER BY r.id DESC LIMIT 1")
    Results findLatestResult();
}
