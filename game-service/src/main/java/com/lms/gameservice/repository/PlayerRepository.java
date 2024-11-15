package com.lms.gameservice.repository;

import com.lms.gameservice.model.Game;
import com.lms.gameservice.model.Player;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByGameIdAndUserId(int gameId, String userId);

    public List<Player> findByGameAndIsActive(Game game, boolean isActive);

}