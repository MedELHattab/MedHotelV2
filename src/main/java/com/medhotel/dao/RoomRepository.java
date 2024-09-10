package com.medhotel.dao;

import com.medhotel.models.Room;

import java.util.List;

public interface RoomRepository {
    void addRoom(Room room);
    Room getRoomById(int id);
    List<Room> getAllRooms();
    void updateRoom(Room room);
    void deleteRoom(int id);
}
