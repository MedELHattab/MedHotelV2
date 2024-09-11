package com.medhotel.dao;

import com.medhotel.db.DatabaseConnection;
import com.medhotel.models.Room;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomRepositoryImpl implements RoomRepository {

    @Override
    public void addRoom(Room room) {
        String getRoomTypeIdSql = "SELECT room_type_id FROM room_types WHERE type_name ILIKE ?";
        String insertRoomSql = "INSERT INTO rooms (room_number, room_type_id, price_per_night, availability) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

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

                insertStmt.setBoolean(4, true);  // Always set availability to true

                insertStmt.executeUpdate();

                // Get the generated room_id
                try (ResultSet rsKeys = insertStmt.getGeneratedKeys()) {
                    if (rsKeys.next()) {
                        room.setRoomId(rsKeys.getInt(1));
                    }
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
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    room = new Room(
                            rs.getInt("room_id"),
                            rs.getString("room_number"),
                            rs.getString("room_type"),
                            rs.getDouble("price_per_night"),
                            rs.getBoolean("availability")
                    );
                }
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

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
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
    public void updateRoom(String roomNumber, Room room) {
        String getRoomTypeIdSql = "SELECT room_type_id FROM room_types WHERE type_name ILIKE ?";
        String updateRoomSql = "UPDATE rooms SET room_number = ?, room_type_id = ?, price_per_night = ?, availability = ? WHERE room_number = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {

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

            // Step 2: Update room in rooms table using room_type_id
            try (PreparedStatement pstmt = conn.prepareStatement(updateRoomSql)) {
                pstmt.setString(1, room.getRoomNumber()); // New room number
                pstmt.setInt(2, roomTypeId);              // Room type ID from lookup
                pstmt.setDouble(3, room.getPricePerNight());// New price per night
                pstmt.setBoolean(4, room.isAvailability()); // New availability

                // Set the original room number for the WHERE clause
                pstmt.setString(5, roomNumber); // The original room number to identify the row

                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteRoom(String roomNumber) {
        String sql = "DELETE FROM rooms WHERE room_number = ?";  // Use room_number as String
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomNumber);  // Set room_number as String
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
