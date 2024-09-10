package com.medhotel.services;

import com.medhotel.dao.RoomRepositoryImpl;  // Use the implementation, not the interface
import com.medhotel.models.Room;
import java.util.List;

public class RoomService {

    // Step 1: Private static instance of RoomService (Singleton instance)
    private static RoomService instance;

    // Step 2: Private RoomRepository for data access (use the implementation)
    private RoomRepositoryImpl roomRepository;

    // Step 3: Private constructor to prevent instantiation
    private RoomService() {
        roomRepository = new RoomRepositoryImpl();  // Use the concrete implementation
    }

    // Step 4: Public static method to provide global access to the singleton instance
    public static synchronized RoomService getInstance() {
        if (instance == null) {
            instance = new RoomService();
        }
        return instance;
    }

    // Step 5: CRUD methods using the repository

    // Create room
    public void addRoom(Room room) {
        roomRepository.addRoom(room);
    }

    // Read all rooms
    public List<Room> getAllRooms() {
        return roomRepository.getAllRooms();
    }

    // Update room
    public void updateRoom(String id ,Room room) {
        roomRepository.updateRoom(id, room);
    }

    // Delete room
    public void deleteRoom(String roomNumber) {
        roomRepository.deleteRoom(roomNumber);
    }

    // Get a room by its ID
    public Room getRoomById(int roomId) {
        return roomRepository.getRoomById(roomId);
    }
}
