
package com.mycompany.oodjassignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;


public abstract class User {
    private String username;
    private String password;
    
    private static FileOperations fop;
    

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.fop = new FileOperations();
    }


    // Public method to display the login menu
    public static void displayLoginMenu() {
        Scanner scanner = new Scanner(System.in);
        String filename;
        
        System.out.println("Welcome to the Login Menu!");
        System.out.println("Select User");
        System.out.println("1.Vendor");
        System.out.println("2.Customer");
        System.out.println("3.Delivery Runner");
        System.out.println("4.Administrator");
        System.out.println("Enter any other key to exit system");
        String userchoice = scanner.nextLine();
        switch (userchoice) {
            case "1":
                filename = "VendorData.txt";
                break;
            case "2":
                filename = "CustomerData.txt";
                break;
            case "3":
                filename = "Runners.txt";
                break;
            case "4":
                filename = "AdministratorData.txt";
            default:
                System.out.println("Goodbye!");
                return;
        }
        
        
        
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();

        System.out.println("Enter your password: ");
        String password = scanner.nextLine();

   boolean loginResult = fop.login(filename,username,password);
   
   if(loginResult){
       
       System.out.println("Login successful!");
       switch (userchoice) {
            case "1":
               try (BufferedReader br = new BufferedReader(new FileReader("VendorData.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 4 && parts[0].trim().equals(username) && parts[1].trim().equals(password)) {
                        String vendorName = parts[2].trim();
                        String vendorRevenueString = parts[3].trim();
                        double vendorRevenue = Double.parseDouble(vendorRevenueString);
                        
                        Vendor vendor = new Vendor(username,password,vendorName,vendorRevenue);
                        vendor.vendorMenu(vendor);
                        break;
                    }
                }
                br.close();
            }catch (IOException e){
            e.printStackTrace();
                
            }
                break;
            case "2":
                
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String USERNAME = parts[0].trim();
                    String PASSWORD = parts[1].trim();
                    String NAME = parts[2].trim();
                    String WALLETString = parts[3].trim();
                
                    double WALLET = Double.parseDouble(WALLETString);
                    
                    if(USERNAME.equals(username)){
                        Customer customer = new Customer(USERNAME,PASSWORD,NAME,WALLET);
                        customer.displayCustomerMainMenu();
                    }
                    
                }
                
            }br.close();
            }catch (IOException e){
            e.printStackTrace();
                
            }
                break;
            case "3":
                Delivery delivery = new Delivery();
                delivery.deliveryScreen(username);
                
                break;
            case "4":
                if(username.equals("admin")&&password.equals("12345")){
                    Administrator admin = new Administrator(username,password);
                    admin.displayMenu();
                }
            try{
                FileReader fileReader = new FileReader(filename);
                BufferedReader br = new BufferedReader(fileReader);
                String line;
                while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String USERNAME = parts[0].trim();
                    String PASSWORD = parts[1].trim();
                    
                    Administrator admin = new Administrator(USERNAME,PASSWORD);
                    
                } 
                }
                 
                br.close();
            }catch(IOException ex){
                System.out.println("Read Error");
            }
            
                
            default:
                System.out.println("Invalid user type");
                return;
        }
        
            displayLoginMenu();
        }
        else{
            System.out.println("Incorrect username or password.");
            displayLoginMenu();
            
        }
     
    }
    
    
    //Getters
    protected String getUsername(){
        return username;
    }
    
    protected String getPassword(){
        return password;
    }
    
    
    
    
    //View notification
    public static void viewNotification(String ID) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Notifications.txt"))) {
            String line;
            int notificationNumber = 1;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].trim().equals(ID.trim())) {
                    // Display the notification (assuming you want to print it)
                    System.out.println(notificationNumber + ". " + parts[1].trim());
                    notificationNumber++;
                }
            }

            if (notificationNumber == 1) {
                System.out.println("No notifications found for the given ID.");
            }
        } catch (IOException e) {
            // Handle the exception (print or log an error message)
            System.out.println("Error reading from Notifications.txt: " + e.getMessage());
        }
    }
    
    //Clear notification
    public static void clearNotification(String ID, int notificationNumber) {
        ArrayList<String> notifications = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Notifications.txt"))) {
            String line;
            int currentNotificationNumber = 1;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2 && parts[0].trim().equals(ID.trim())) {
                    // Check if the notification number matches the one to be cleared
                    if (currentNotificationNumber != notificationNumber) {
                        notifications.add(line);
                    }
                    currentNotificationNumber++;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from Notifications.txt: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Notifications.txt"))) {
            for (String notification : notifications) {
                writer.write(notification);
                writer.newLine();
            }
            System.out.println("Notification cleared successfully.");
        } catch (IOException e) {
            System.out.println("Error writing to Notifications.txt: " + e.getMessage());
        }
    }
    
    //Send notification
    public static void sendNotification(String ID, String notification) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Notifications.txt", true))) {
            // Append the new notification to the file
            writer.write(ID + "," + notification);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to Notifications.txt: " + e.getMessage());
        }
    }
    
}