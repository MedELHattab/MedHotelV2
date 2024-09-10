package com.medhotel.dao;

import com.medhotel.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomRepositoryImpl implements RoomRepository {
    private final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Update as needed
    private final String USER = "postgres"; // Your PostgreSQL username
    private final String PASSWORD = "password"; // Your PostgreSQL password

    public RoomRepositoryImpl() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "room_id SERIAL PRIMARY KEY," +
                    "room_number VARCHAR(50)," +
                    "room_type VARCHAR(50)," +
                    "price_per_night DECIMAL," +
                    "availability BOOLEAN" +
                    ")";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, availability) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setBoolean(4, room.isAvailability());
            pstmt.executeUpdate();

            // Get the generated room_id
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                room.setRoomId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Room getRoomById(int id) {
        Room room = null;
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night"),
                        rs.getBoolean("availability")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return room;
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),
                        rs.getDouble("price_per_night"),
                        rs.getBoolean("availability")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }

    @Override
    public void updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, price_per_night = ?, availability = ? WHERE room_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setString(2, room.getRoomType());
            pstmt.setDouble(3, room.getPricePerNight());
            pstmt.setBoolean(4, room.isAvailability());
            pstmt.setInt(5, room.getRoomId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
