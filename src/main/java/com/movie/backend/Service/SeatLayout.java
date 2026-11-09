package com.movie.backend.Service;

import java.util.ArrayList;
import java.util.List;

// Seats are laid out in rows of 12; row A is nearest the screen and the
// farthest rows are PREMIUM, then GOLD, then EXECUTIVE.
public final class SeatLayout {

    public static final int SEATS_PER_ROW = 12;
    public static final int MAX_ROWS = 26;
    public static final int MAX_SEATS = SEATS_PER_ROW * MAX_ROWS;

    public record Row(String label, String category, int seatCount) {
    }

    private SeatLayout() {
    }

    public static List<Row> rows(int totalSeats) {
        int rowCount = (int) Math.ceil(totalSeats / (double) SEATS_PER_ROW);
        int tierRows = rowCount >= 3 ? Math.max(1, Math.round(rowCount * 0.2f)) : 0;
        List<Row> rows = new ArrayList<>();
        for (int index = 0; index < rowCount; index++) {
            int seatCount = Math.min(SEATS_PER_ROW, totalSeats - index * SEATS_PER_ROW);
            int fromBack = rowCount - 1 - index;
            String category = fromBack < tierRows ? "PREMIUM"
                    : fromBack < tierRows * 2 ? "GOLD" : "EXECUTIVE";
            rows.add(new Row(String.valueOf((char) ('A' + index)), category, seatCount));
        }
        return rows;
    }

    public static boolean isValidSeat(String label, int totalSeats) {
        if (label == null || label.length() < 2) {
            return false;
        }
        int rowIndex = label.charAt(0) - 'A';
        int number;
        try {
            number = Integer.parseInt(label.substring(1));
        } catch (NumberFormatException e) {
            return false;
        }
        List<Row> rows = rows(totalSeats);
        return rowIndex >= 0 && rowIndex < rows.size()
                && number >= 1 && number <= rows.get(rowIndex).seatCount()
                && label.equals(rows.get(rowIndex).label() + number);
    }
}
