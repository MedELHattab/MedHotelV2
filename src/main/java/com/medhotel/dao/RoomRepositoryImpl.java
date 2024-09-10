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
        String getRoomTypeIdSql = "SELECT room_type_id FROM room_types WHERE type_name ILIKE ?";
        String insertRoomSql = "INSERT INTO rooms (room_number, room_type_id, price_per_night, availability) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // Step 1: Get room_type_id from room_types table
            int roomTypeId = 0;
            try (PreparedStatement getTypeStmt = conn.prepareStatement(getRoomTypeIdSql)) {
                getTypeStmt.setString(1, room.getRoomType()); // e.g., 'Single', 'Double'
                ResultSet rs = getTypeStmt.executeQuery();

                if (rs.next()) {
                    roomTypeId = rs.getInt("room_type_id");
                } else {
                    System.out.println("Room type not found.");
                    return;  // Exit if room type is not found
                }
            }

            // Step 2: Insert room into rooms table using roomTypeId
            try (PreparedStatement insertStmt = conn.prepareStatement(insertRoomSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, room.getRoomNumber());
                insertStmt.setInt(2, roomTypeId);  // Set the room_type_id
                insertStmt.setDouble(3, room.getPricePerNight());

                // Step 3: Force availability to true
                insertStmt.setBoolean(4, true);  // Always set availability to true

                insertStmt.executeUpdate();

                // Get the generated room_id
                ResultSet rsKeys = insertStmt.getGeneratedKeys();
                if (rsKeys.next()) {
                    room.setRoomId(rsKeys.getInt(1));
                }
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
        String sql = "SELECT r.room_id, r.room_number, rt.type_name AS room_type, r.price_per_night, r.availability " +
                "FROM rooms r " +
                "JOIN room_types rt ON r.room_type_id = rt.room_type_id";  // Join with room_types to get room_type

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = new Room(
                        rs.getInt("room_id"),
                        rs.getString("room_number"),
                        rs.getString("room_type"),  // Get the type_name from room_types table
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
    public void deleteRoom(String room_number) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";  // Use room_number as String
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room_number);  // Set room_number as String
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
