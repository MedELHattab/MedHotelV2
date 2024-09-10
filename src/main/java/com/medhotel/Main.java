package com.medhotel;

import com.medhotel.models.Room;
import com.medhotel.models.RoomType;
import com.medhotel.services.RoomService;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        RoomService roomService = RoomService.getInstance();  // Singleton instance
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Room");
            System.out.println("2. View All Rooms");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Add Room Logic
                    Room newRoom = new Room();

                    System.out.print("Enter Room Number: ");
                    String roomNumber = scanner.next();
                    newRoom.setRoomNumber(roomNumber);

                    System.out.print("Enter Room Type (e.g., Single, Double): ");
                    String roomTypeInput = scanner.next().toUpperCase();
                    newRoom.setRoomType(roomTypeInput); // Use the string here, e.g., 'Single', 'Double'

                    System.out.print("Enter Room Price: ");
                    double price = scanner.nextDouble();
                    newRoom.setPricePerNight(price);

                    roomService.addRoom(newRoom); // This will handle the room_type_id lookup and insertion
                    System.out.println("Room added successfully.");
                    break;

                case 2:
                    // View all rooms
//                    System.out.println("Room List:");
//                    List<Room> rooms = roomService.getAllRooms();
//                    for (Room room : rooms) {
//                        System.out.println(room);
//                    }
//                    break;

                case 3:
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }

        scanner.close();
    }
}
