
package com.mycompany.oodjassignment;

import java.io.*;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;

public class Administrator extends User {
    private static final String CUSTOMER_FILE_PATH = "CustomerData.txt";
    private static final String VENDOR_FILE_PATH = "VendorData.txt";
    private static final String RUNNER_FILE = "RunnerData.txt";
    private static final String ORDER_HISTORY_FILE = "OrderHistory.txt";
    private static final String NOTIFICATION_FILE = "NotificationData.txt";

    // Constructor
    public Administrator(String userID, String password) {
        super (userID, password);
    }
     public void displayMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Administrator Menu:");
            System.out.println("1. Add User");
            System.out.println("2. Read User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Top-up Customer Credit");
            System.out.println("6. Generate Transaction Receipt");
            System.out.println("7. Send Receipt to Customer");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> addUserMenu();
                case 2 -> readUserMenu();
                case 3 -> updateUserMenu();
                case 4 -> deleteUserMenu();
                case 5 -> topUpCustomerCreditMenu();
                case 6 -> generateTransactionReceiptMenu();
                case 7 -> sendReceiptToCustomerMenu();
                case 0 -> {
                    System.out.println("Exiting Administrator Menu.");
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
     // Add a new user (CRUD - Create)
    public void addUserMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose user type:");
        System.out.println("1. Customer");
        System.out.println("2. Vendor");
        System.out.println("3. Runner");
        System.out.print("Enter your choice: ");
        int userTypeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        String userType;
        switch (userTypeChoice) {
            case 1 -> userType = "customer";
            case 2 -> userType = "vendor";
            case 3 -> userType = "runner";
            default -> {
                System.out.println("Invalid user type choice. User not added.");
                return;
            }
        }

        // Get user details
        System.out.print("Enter userID: ");
        String userID = scanner.nextLine();
        System.out.print("Enter userName: ");
        String userName = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        double wallet = 0.0;
        if (userType.equals("customer")) {
            System.out.print("Enter wallet balance: ");
            wallet = scanner.nextDouble();
        }

        addUserMenu(userType, userID, userName, password, wallet);
    }

    // Add a new customer/vendor/runner (CRUD - Create)
    private void addUserMenu(String type, String userID, String userName, String password, double wallet) {
        String filePath;
        String data = userID + "," + userName + "," + password + (type.equals("customer") ? "," + wallet : "") + "\n";

        switch (type) {
            case "customer" -> filePath = CUSTOMER_FILE_PATH;
            case "vendor" -> filePath = VENDOR_FILE_PATH;
            default -> {
                System.out.println("Invalid user type");
                return;
            }
        }

        // Check if the customer/vendor userID already exists
        if (isUserIDExists(filePath, userID)) {
            System.out.println(type + " with userID '" + userID + "' already exists. Cannot add.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(data);
            System.out.println(type.toUpperCase() + " added successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to " + filePath);
        }
    }

    // Helper method to check if a userID already exists in a file
    private boolean isUserIDExists(String filePath, String userID) {
        	try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(userID + ",")) {
                    return true; // userID already exists
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from " + filePath);
        }
        return false; // userID does not exist
    }
    // Read a user (CRUD - Read)
    public void readUserMenu (String type, String userID) {
        String filePath;
        switch (type) {


            case "customer" -> filePath = CUSTOMER_FILE_PATH;
            case "vendor" -> filePath = VENDOR_FILE_PATH;
            case "runner" -> filePath = RUNNER_FILE;
            default -> {
                System.out.println("Invalid user type");
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(userID + ",")) {
                    System.out.println(currentLine);
                    return;
                }
            }
            System.out.println(type.toUpperCase() + " not found.");
        } catch (IOException e) {
            System.err.println("Error reading from " + filePath);
        }
    }

    // Update a customer/vendor/runner (CRUD - Update)
    public void updateUserMenu(String type, String userID, String newUserName, String newPassword, Double newWalletBalance) {
        String filePath;

        switch (type) {
            case "customer" -> filePath = CUSTOMER_FILE_PATH;
            case "vendor" -> filePath = VENDOR_FILE_PATH;
            case "runner" -> filePath = RUNNER_FILE;
            default -> {
                System.out.println("Invalid user type");
                return;
            }
        }

        File inputFile = new File(filePath);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            boolean updated = false;

            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(",");
                if (data[0].equals(userID)) {
                    data[1] = newUserName;
                    data[2] = newPassword;
                    if (type.equals("customer") && newWalletBalance != null) {
                        data[3] = String.valueOf(newWalletBalance);
                    }
                    currentLine = String.join(",", data);
                    updated = true;
                }
                writer.write(currentLine + "\n");
            }

            if (!inputFile.delete()) {
                System.out.println("Could not delete the original file");
                return;
            }
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Could not rename the temp file");
            }

            if (updated) {
                System.out.println(type.toUpperCase() + " updated successfully.");
            } else {
                System.out.println(type.toUpperCase() + " not found.");
            }

        } catch (IOException e) {
            System.err.println("Error processing the file.");
        }
    }

    // Delete a customer/vendor/runner (CRUD - Delete)
    public void deleteUserMenu(String type, String userID) {
        String filePath;

        switch (type) {
            case "customer" -> filePath = CUSTOMER_FILE_PATH;
            case "vendor" -> filePath = VENDOR_FILE_PATH;
            case "runner" -> filePath = RUNNER_FILE;
            default -> {
                System.out.println("Invalid user type");
                return;
            }
        }

        File inputFile = new File(filePath);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            boolean deleted = false;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.startsWith(userID + ",")) {
                    writer.write(currentLine + "\n");
                } else {
                    deleted = true;
                }
            }

            if (!inputFile.delete()) {
                System.out.println("Could not delete the original file");
                return;
            }
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Could not rename the temp file");
            }

            if (deleted) {
                System.out.println(type.toUpperCase() + " deleted successfully.");
            } else {
                System.out.println(type.toUpperCase() + " not found.");
            }

        } catch (IOException e) {
            System.err.println("Error processing the file.");
        }
    }
 // Top-up customer credit
    public void topUpCustomerCreditMenu(String customerID, double amount) {
        File inputFile = new File(CUSTOMER_FILE_PATH);
        File tempFile = new File("temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            boolean updated = false;

            while ((currentLine = reader.readLine()) != null) {
                String[] data = currentLine.split(",");
                if (data[0].equals(customerID)) {
                    double currentBalance = Double.parseDouble(data[3]);
                    currentBalance += amount;
                    data[3] = String.valueOf(currentBalance);
                    currentLine = String.join(",", data);
                    updated = true;
                }
                writer.write(currentLine + "\n");
            }

            if (!inputFile.delete()) {
                System.out.println("Could not delete the original file");
                return;
            }
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Could not rename the temp file");
            }

            if (updated) {
                System.out.println("Customer credit updated successfully.");
            } else {
                System.out.println("Customer not found.");
            }

        } catch (IOException e) {
            System.err.println("Error processing the file.");
        }
    }

    // Generate transaction receipt
    public String generateTransactionReceiptMenu(String orderID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDER_HISTORY_FILE))) {
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.startsWith(orderID + ",")) {
                    // Assuming the format is OrderID,Details,Amount
                    return "Receipt: " + currentLine;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading from " + ORDER_HISTORY_FILE);
        }
        return "Receipt not found for order: " + orderID;
    }

    // Send receipt to customer through notification
    public void sendReceiptToCustomerMenu(String customerID, String receipt) 
    {
        // For simplicity, we write the receipt to a notification file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOTIFICATION_FILE, true))) {
            writer.write("CustomerID: " + customerID + " - " + receipt + "\n");
            System.out.println("Receipt sent to customer: " + customerID);
        } catch (IOException e) {
            System.err.println("Error writing to " + NOTIFICATION_FILE);
        }
    }

    private void readUserMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void updateUserMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void deleteUserMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void topUpCustomerCreditMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void generateTransactionReceiptMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void sendReceiptToCustomerMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void String(String userID, String password) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

   
}