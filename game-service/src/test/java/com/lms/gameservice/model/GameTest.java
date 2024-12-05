package com.lms.gameservice.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void testGameGettersAndSetters() {
        Game game = new Game();

        game.setId(1);
        game.setName("Test Game");
        game.setEntryFee(BigDecimal.valueOf(50));
        game.setStartDate(LocalDateTime.of(2024, 12, 1, 10, 0));
        game.setStatus("CREATED");
        game.setTotalPot(BigDecimal.valueOf(1000));
        game.setCurrentRound(2);

        assertEquals(1, game.getId());
        assertEquals("Test Game", game.getName());
        assertEquals(BigDecimal.valueOf(50), game.getEntryFee());
        assertEquals(LocalDateTime.of(2024, 12, 1, 10, 0), game.getStartDate());
        assertEquals("CREATED", game.getStatus());
        assertEquals(BigDecimal.valueOf(1000), game.getTotalPot());
        assertEquals(2, game.getCurrentRound());
    }
}
