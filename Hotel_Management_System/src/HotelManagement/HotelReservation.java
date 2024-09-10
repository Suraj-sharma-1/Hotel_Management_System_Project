package HotelManagement;

import java.sql.DriverManager;
import java.sql.SQLException;
import  java.sql.Connection;
import  java.sql.Statement;
import  java.sql.ResultSet;
import java.util.Scanner;


public class HotelReservation {

    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
   private static final  String username  ="root";
   private static final  String password ="Prem";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        //System.out.println("Hello world!");

        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Drivers loaded successfully ");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try
        {
            Connection con = DriverManager.getConnection(url,username,password);
          //  System.out.println("Connection estabalished successfully : ");
            while (true){
                System.out.println();
                System.out.println("Hotel Management");
                Scanner sc = new Scanner(System.in);
                System.out.println("1. Reserve a room: ");
                System.out.println("2. View Reservation :");
                System.out.println("3. Get Room number :");
                System.out.println("4. Update Reservation :");
                System.out.println("5. Delete Reservation :");
                System.out.println("0. Exit :");
                System.out.println("Choose an Option :");
                int choice = sc.nextInt();
                switch (choice){
                    case 1:
                    reserveRoom(con,sc);
                    break;

                    case 2:
                        viewReservations(con);
                        break;
                    case 3:
                        getRoomnumber(con,sc);
                        break;
                    case 4:
                        updateReservation(con,sc);
                        break;
                    case 5:
                        deleteReservation(con,sc);
                        break;

                    case 0:
                        exit();
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice ,Try again : ");





                }


            }


        }
        catch (SQLException e1){
            System.out.println(e1.getMessage());

        }
        catch (InterruptedException e2){
            throw new RuntimeException(e2);
        }




    }
    private  static  void  reserveRoom(Connection con,Scanner sc){

        try{
            System.out.println("Enter guest name:");
            String guestName = sc.next();
            sc.nextLine();
            System.out.println("Enter room number");
            int roomNumber = sc.nextInt();
            System.out.println("Enter the contact number ");
            String contactNumber = sc.next();
            String sql = "Insert into reservations (guest_name,room_number,contact_number)"+
                    "Values('"+guestName+"',"+roomNumber+",'"+contactNumber+"')";
            try(Statement statement = con.createStatement()){
                int affectedRows = statement.executeUpdate(sql);
                if(affectedRows>0){
                    System.out.println("Reservation successful ");
                }
                else{
                    System.out.println("Reservation failed ");
                }
            }


        }catch (SQLException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }
    private static  void viewReservations(Connection con) throws SQLException{
        String sql  = "Select reservation_id,guest_name,room_number ,contact_number,reservation_date from reservations ";
        try (Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");
                String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                // Format and display the reservation data in a table-like format
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }
    private static void getRoomnumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }


    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }


}
