package com.medhotel.services;

import com.medhotel.dao.ReservationRepository;
import com.medhotel.dao.ReservationRepositoryImpl;
import com.medhotel.models.Reservation;

import java.util.List;

public class ReservationService {
    private ReservationRepositoryImpl reservationRepository;
    private static ReservationService instance ;
    // Private constructor for Singleton
    private ReservationService() {
        reservationRepository = new ReservationRepositoryImpl();
    }

    // Static method to get the singleton instance
    public static ReservationService getInstance() {
        if (instance == null) {
            synchronized (ReservationService.class) {
                if (instance == null) {
                    instance = new ReservationService();
                }
            }
        }
        return instance;
    }



    public void addReservation(Reservation reservation) {
        reservationRepository.addReservation(reservation);
    }

    public Reservation getReservationById(int id) {
        return reservationRepository.getReservationById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.getAllReservations();
    }

    public void updateReservation(int reservationId, Reservation reservation) {
        reservationRepository.updateReservation(reservationId, reservation);
    }

    public void deleteReservation(int reservationId) {
        reservationRepository.deleteReservation(reservationId);
    }
}

