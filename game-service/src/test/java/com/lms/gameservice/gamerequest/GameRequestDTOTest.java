package com.lms.gameservice.gamerequest;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class GameRequestDTOTest {

    @Test
    void testGameRequestDTOGettersAndSetters() {
        GameRequestDTO dto = new GameRequestDTO();

        dto.setName("Test Game");
        dto.setEntryFee(BigDecimal.valueOf(50));
        dto.setWeeksTillStartDate(2);

        assertEquals("Test Game", dto.getName());
        assertEquals(BigDecimal.valueOf(50), dto.getEntryFee());
        assertEquals(2, dto.getWeeksTillStartDate());
    }

    @Test
    void testGameRequestDTOConstructor() {
        GameRequestDTO dto = new GameRequestDTO("Sample Game", BigDecimal.valueOf(100), 4);

        assertEquals("Sample Game", dto.getName());
        assertEquals(BigDecimal.valueOf(100), dto.getEntryFee());
        assertEquals(4, dto.getWeeksTillStartDate());
    }
}
