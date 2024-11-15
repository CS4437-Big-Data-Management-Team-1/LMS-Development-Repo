package com.lms.gameservice.repository;

import com.lms.gameservice.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Integer> {
    @Query("SELECT g FROM Game g WHERE g.startDate > :currentDate")
    List<Game> findJoinableGames(@Param("currentDate") LocalDateTime currentDate);

    List<Game> findByStatus(String status);
}