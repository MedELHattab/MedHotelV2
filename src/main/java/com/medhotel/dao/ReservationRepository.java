package com.medhotel.dao;

import com.medhotel.models.Reservation;

import java.util.List;

public interface ReservationRepository {
    void addReservation(Reservation reservation);
    Reservation getReservationById(int id);
    List<Reservation> getAllReservations();
    void updateReservation(int reservationId, Reservation reservation);
    void cancelReservation(int reservationId);
}

