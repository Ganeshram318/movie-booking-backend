package com.movie.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// The unique constraint is the database-level guard against double booking a seat
@Entity
@Table(name = "booked_seat", uniqueConstraints = @UniqueConstraint(
        name = "uk_booked_seat_showtime_seat",
        columnNames = {"showtime_id", "seat_label"}))
@Getter
@Setter
@NoArgsConstructor
public class BookedSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    private ShowTime showtime;

    @Column(name = "seat_label", nullable = false, length = 5)
    private String seatLabel;
}
