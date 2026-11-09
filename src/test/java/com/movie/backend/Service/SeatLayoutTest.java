package com.movie.backend.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class SeatLayoutTest {

    @Test
    void rowsMatchCapacityWithPartialLastRow() {
        List<SeatLayout.Row> rows = SeatLayout.rows(30);

        assertEquals(3, rows.size());
        assertEquals(30, rows.stream().mapToInt(SeatLayout.Row::seatCount).sum());
        assertEquals(6, rows.get(2).seatCount());
        assertEquals("C", rows.get(2).label());
    }

    @Test
    void backRowsArePremiumThenGold() {
        List<SeatLayout.Row> rows = SeatLayout.rows(120);

        assertEquals("EXECUTIVE", rows.get(0).category());
        assertEquals("GOLD", rows.get(7).category());
        assertEquals("PREMIUM", rows.get(9).category());
    }

    @Test
    void validatesSeatLabelsAgainstCapacity() {
        assertTrue(SeatLayout.isValidSeat("A1", 30));
        assertTrue(SeatLayout.isValidSeat("C6", 30));
        assertFalse(SeatLayout.isValidSeat("C7", 30));
        assertFalse(SeatLayout.isValidSeat("D1", 30));
        assertFalse(SeatLayout.isValidSeat("A0", 30));
        assertFalse(SeatLayout.isValidSeat("A01", 30));
        assertFalse(SeatLayout.isValidSeat("a1", 30));
        assertFalse(SeatLayout.isValidSeat("A", 30));
        assertFalse(SeatLayout.isValidSeat(null, 30));
    }
}
