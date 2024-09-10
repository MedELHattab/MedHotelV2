package com.medhotel;

import com.medhotel.models.Room;
import com.medhotel.models.RoomType;
import com.medhotel.services.RoomService;

import java.util.List;
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
            System.out.println("3. Delete Room");
            System.out.println("4. Update Room");
            System.out.println("5. Exit");
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
                    System.out.println("Room List:");
                    List<Room> rooms = roomService.getAllRooms();
                    for (Room room : rooms) {
                        System.out.println(room);
                    }
                    break;

                case 3:
                    System.out.print("Enter Room Number: ");
                    String deletedRoomNumber = scanner.next();  // Get the room number as String

                    try {
                         // Convert to int
                        roomService.deleteRoom(deletedRoomNumber);  // Use the integer version
                        System.out.println("Room deleted successfully.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid room number format or room Number Not Found. Please enter a valid number.");
                    }
                    break;

                case 4:
                    // Update Room Logic
                    Room updatedRoom = new Room();
                    System.out.print("Enter The Desired Room Number: ");
                    String desiredRoomNumber = scanner.next();

                    System.out.print("Enter The New Room Number: ");
                    String updatedRoomNumber = scanner.next();
                    updatedRoom.setRoomNumber(updatedRoomNumber);

                    System.out.print("Enter The New Room Type (e.g., Single, Double): ");
                    String updatedRoomTypeInput = scanner.next();
                    updatedRoom.setRoomType(updatedRoomTypeInput); // Use the string here, e.g., 'Single', 'Double'

                    System.out.print("Enter The New Room Price: ");
                    double updatedPrice = scanner.nextDouble();
                    updatedRoom.setPricePerNight(updatedPrice);

                    System.out.print("Is the room available? (true/false): ");
                    boolean updatedAvailability = scanner.nextBoolean();
                    updatedRoom.setAvailability(updatedAvailability);

                    roomService.updateRoom(desiredRoomNumber, updatedRoom); // Call the update function
                    System.out.println("Room updated successfully.");
                    break;


                case 5:
                    exit = true;
                    break;

                default:
                    System.out.println("Invalid option, please try again.");
            }
        }

        scanner.close();
    }
}
