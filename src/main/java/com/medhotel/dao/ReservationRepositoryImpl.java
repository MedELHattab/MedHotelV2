package com.medhotel.dao;

import com.medhotel.db.DatabaseConnection;
import com.medhotel.models.Customer;
import com.medhotel.models.Holiday;
import com.medhotel.models.Reservation;
import com.medhotel.services.HolidayService;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class ReservationRepositoryImpl implements ReservationRepository {

    @Override
    public void addReservation(Reservation reservation) {
        String roomPriceQuery = "SELECT price_per_night FROM rooms WHERE room_id = ?";
        String customerQuery = "SELECT * FROM customers WHERE customer_id = ?";
        String holidayQuery = "SELECT holiday_date FROM holidays WHERE holiday_date BETWEEN ? AND ?";
        String sql = "INSERT INTO reservations (customer_id, room_id, check_in_date, check_out_date, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement roomPriceStmt = conn.prepareStatement(roomPriceQuery);
             PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
             PreparedStatement holidayStmt = conn.prepareStatement(holidayQuery);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Step 1: Validate check-in and check-out dates
            if (reservation.getCheckInDate().isAfter(reservation.getCheckOutDate())) {
                throw new IllegalArgumentException("Check-in date cannot be after check-out date.");
            }

            // Step 2: Check if customer exists using Optional
            customerStmt.setInt(1, reservation.getCustomerId());
            ResultSet rsCustomer = customerStmt.executeQuery();
            Optional<Customer> customerOpt = Optional.empty();
            if (rsCustomer.next()) {
                Customer customer = new Customer(
                        rsCustomer.getInt("customer_id"),
                        rsCustomer.getString("first_name"),
                        rsCustomer.getString("last_name"),
                        rsCustomer.getString("email"),
                        rsCustomer.getString("phone"),
                        rsCustomer.getString("address")
                );
                customerOpt = Optional.of(customer);
            }
            if (customerOpt.isEmpty()) {
                throw new IllegalArgumentException("Customer not found for ID: " + reservation.getCustomerId());
            }

            // Step 3: Get the room price using Optional
            roomPriceStmt.setInt(1, reservation.getRoomId());
            ResultSet rsRoomPrice = roomPriceStmt.executeQuery();
            Optional<Double> roomPriceOpt = Optional.empty();
            if (rsRoomPrice.next()) {
                roomPriceOpt = Optional.of(rsRoomPrice.getDouble("price_per_night"));
            }
            if (roomPriceOpt.isEmpty()) {
                throw new IllegalArgumentException("Room not found for ID: " + reservation.getRoomId());
            }
            double roomPrice = roomPriceOpt.get();

            // Step 4: Query holidays between check-in and check-out dates
            holidayStmt.setDate(1, Date.valueOf(reservation.getCheckInDate()));
            holidayStmt.setDate(2, Date.valueOf(reservation.getCheckOutDate()));
            ResultSet rsHolidays = holidayStmt.executeQuery();
            Set<LocalDate> holidays = new HashSet<>();
            while (rsHolidays.next()) {
                holidays.add(rsHolidays.getDate("holiday_date").toLocalDate());
            }

            // Step 5: Calculate total price considering holidays
            LocalDate checkInDate = reservation.getCheckInDate();
            LocalDate checkOutDate = reservation.getCheckOutDate();
            double totalPrice = 0.0;

            for (LocalDate date = checkInDate; !date.isAfter(checkOutDate); date = date.plusDays(1)) {
                if (holidays.contains(date)) {
                    totalPrice += roomPrice * 2;  // Double the price if it's a holiday
                } else {
                    totalPrice += roomPrice;  // Regular price if not a holiday
                }
            }

            reservation.setTotalPrice(totalPrice);

            // Step 6: Insert the reservation
            pstmt.setInt(1, reservation.getCustomerId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, Date.valueOf(checkInDate));
            pstmt.setDate(4, Date.valueOf(checkOutDate));
            pstmt.setDouble(5, totalPrice);

            pstmt.executeUpdate();

            // Get the generated reservation_id
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    reservation.setReservationId(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






    @Override
    public Reservation getReservationById(int id) {
        Reservation reservation = null;
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    reservation = new Reservation(
                            rs.getInt("reservation_id"),
                            rs.getInt("customer_id"),
                            rs.getInt("room_id"),
                            rs.getDate("check_in_date").toLocalDate(),
                            rs.getDate("check_out_date").toLocalDate(),
                            rs.getDouble("total_price")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservation;
    }

    @Override
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("room_id"),
                        rs.getDate("check_in_date").toLocalDate(),
                        rs.getDate("check_out_date").toLocalDate(),
                        rs.getDouble("total_price")
                );
                reservations.add(reservation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    @Override
    public void updateReservation(int reservationId, Reservation reservation) {
        String sql = "UPDATE reservations SET customer_id = ?, room_id = ?, check_in_date = ?, check_out_date = ?, total_price = ? WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservation.getCustomerId());
            pstmt.setInt(2, reservation.getRoomId());
            pstmt.setDate(3, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setDouble(5, reservation.getTotalPrice());
            pstmt.setInt(6, reservationId);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelReservation(int reservationId) {
        String reservationQuery = "SELECT customer_id, check_in_date, total_price FROM reservations WHERE reservation_id = ?";
        String insertRefundQuery = "INSERT INTO refunds (reservation_id, customer_id, refund) VALUES (?, ?, ?)";
        String deleteReservationQuery = "UPDATE reservations SET status = 'canceled' WHERE reservation_id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement reservationStmt = conn.prepareStatement(reservationQuery);
             PreparedStatement refundStmt = conn.prepareStatement(insertRefundQuery);
             PreparedStatement deleteStmt = conn.prepareStatement(deleteReservationQuery)) {

            // Step 1: Fetch reservation details
            reservationStmt.setInt(1, reservationId);
            ResultSet rs = reservationStmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                LocalDate checkInDate = rs.getDate("check_in_date").toLocalDate();
                double totalPrice = rs.getDouble("total_price");

                // Step 2: Calculate the refund
                LocalDate currentDate = LocalDate.now();
                double refundAmount = 0.0;

                // Check if cancellation is 3 days before check-in date
                if (currentDate.isBefore(checkInDate.minusDays(3))) {
                    refundAmount = totalPrice * 0.5;  // 50% refund
                }

                // Step 3: Insert refund into the refunds table
                refundStmt.setInt(1, reservationId);
                refundStmt.setInt(2, customerId);
                refundStmt.setDouble(3, refundAmount);
                refundStmt.executeUpdate();

                // Step 4: Delete the reservation
                deleteStmt.setInt(1, reservationId);
                deleteStmt.executeUpdate();

                System.out.println("Reservation cancelled successfully. Refund: $" + refundAmount);
            } else {
                System.out.println("Reservation not found for ID: " + reservationId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

