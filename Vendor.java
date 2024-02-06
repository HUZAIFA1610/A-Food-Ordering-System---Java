
package com.mycompany.oodjassignment;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Vendor extends User{
    private String vendorID = getUsername();
    private String password = getPassword();
    private String vendorName;
    private double Revenue;
    
    private Map<String, String> vendorCredentials;
    private Map<String, String> vendorRestaurantNames;
    private Map<String, Restaurant> vendorRestaurantMap;
    private Map<String, Restaurant> restaurants;
    private Scanner scanner;
    private FileOperations fop;
    
    
    public Vendor(String username,String password,String VendorName, double Revenue){
        super(username,password);
        this.vendorName = VendorName;
        this.vendorCredentials = new HashMap<>();
        this.vendorRestaurantNames = new HashMap<>();
        this.scanner = new Scanner(System.in);
        this.vendorRestaurantMap = new HashMap<>();
        this.restaurants = new HashMap<>();
        this.fop = new FileOperations();
        loadVendorCredentials();
    }
    
    public double getRevenue(){
        return Revenue;
    }
    
    public void view_menu(){
        fop.SearchFromMenu("Menu.txt",this.vendorName);
    }
    
    public String getVendorID(){
        return vendorID;
    }
    
    public String getVendorName() {
        return vendorName;
    }
    
    
    
    
    
    
    
    

    private void loadVendorCredentials() {
        try (BufferedReader br = new BufferedReader(new FileReader("VendorData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 3) {
                    String vendorID = credentials[0];
                    String password = credentials[1];
                    String vendorName = credentials[2];
                    vendorCredentials.put(vendorID, password);
                    vendorRestaurantNames.put(vendorID, vendorName);
                }
//                vendorCredentials.put(credentials[0], credentials[1]); // Assuming the file format is "vendorID,password"
            }
        } catch (IOException e) {
            System.out.println("Error loading vendor credentials: " + e.getMessage());
        }
    }
    
    public void vendorMenu(Vendor vendor) throws IOException{
        System.out.println("\n\n\t\t\t\t\t\t\tWELCOME TO VENDOR MENU");
        System.out.println("\n\n\nHere Are The Functionalities for Vendor");
        System.out.println("\n\n1. CREATE ITEM");
        System.out.println("2. DISPLAY ITEM");
        System.out.println("3. UPDATE ITEM");
        System.out.println("4. DELETE ITEM");
        System.out.println("5. ACCEPT/CANCEL ORDER");
        System.out.println("6. CHECK PENDING ORDERS");
        System.out.println("7. CHECK ORDER HISTORY");
        System.out.println("8. CHECK DAILY ORDER HISTORY");
        System.out.println("9. CHECK MONTHLY ORDER HISTORY");
        System.out.println("10. READ CUSTOMER REVIEW");
        System.out.println("11. REVENUE DASHBOARD");
        System.out.println("12. NOTIFICATIONS");
        System.out.println("0. LOGOUT");
        
        System.out.println("\nEnter Your Choice: ");
        Scanner scanner = new Scanner(System.in);
        int vendorChoice = scanner.nextInt();
        scanner.nextLine();
        
        ArrayList<Order> orderList = Order.readOrdersFromFile();
      //  Vendor vendor = new Vendor(vendorID, vendorPassword, vendorName,vendorRevenue);
        switch (vendorChoice){
            case 1:
                vendor.createRestaurantItem(vendor);
                break;
            case 2:
                System.out.println("Enter Vendor ID: ");
                String displayVendor = scanner.nextLine();
                displayItem(displayVendor,vendor);
                break;
            case 3:
                System.out.println("Enter Vendor Name: ");
                String updateVendor = scanner.nextLine();
                System.out.println("Enter item code: ");
                String updateItemCode = scanner.nextLine();
                vendor.updateItemPrice(updateVendor, updateItemCode, vendor);
                break;
            case 4:
                System.out.println("Enter Vendor Name: ");
                String deleteVendor = scanner.nextLine();
                System.out.println("Enter item code: ");
                String deleteItemCode = scanner.nextLine();
                vendor.deleteItem(deleteVendor, deleteItemCode, vendor);
                break;
            case 5:
                System.out.println("1. Accept pending order");
                System.out.println("2. Cancel order");
                int acceptCancelChoice = scanner.nextInt();
                
                if(acceptCancelChoice == 1){
                        fop.printPendingOrders(this.vendorID);
                        System.out.println("Enter Order ID of order to accept: ");
                        String orderID = scanner.next();
                        if (Order.CheckStatus(orderID).equals("Pending")){
                        //add to vendor revenue and deduct from customer wallet
                            fop.paymentVendor(orderID);
                            fop.paymentCustomer(orderID);
                            for (Order order : orderList) {
                            if (order.getOrderID().equals(orderID)) {
                                sendNotification(order.getCustomer().getCustomerID(),"Vendor " + this.vendorID +", "+vendorName+" accepted the order with ID " + orderID);
                            }
                        }
                        //accepts pending order
                            Order.updateOrderStatus(orderID,"Accepted");
                            System.out.println("Order accepted! Funds have been added to your revenue.");
                    }
                }
                else if(acceptCancelChoice == 2){
                      
                    fop.printAcceptedOrders(this.vendorID);
                    fop.printPendingOrders(this.vendorID);
                    System.out.println("Enter Order ID of order to cancel: ");
                    String orderId = scanner.next();
                    if (Order.CheckStatus(orderId).equals("Accepted")){
                        //deduct from vendor revenue and refund customer
                        fop.refundVendor(orderId);
                        fop.refundCustomer(orderId);
                        for (Order order : orderList) {
                            if (order.getOrderID().equals(orderId)) {
                                sendNotification(order.getCustomer().getCustomerID(),"Vendor " + this.vendorID +", "+vendorName+" cancelled the order with ID " + orderId);
                            }
                        }
                        //cancels accepted order
                        Order.updateOrderStatus(orderId,"Cancelled");
                        System.out.println("Order cancelled successfully! Funds have been refunded to your wallet.");
                        }
                    else if (Order.CheckStatus(orderId).equals("Pending")){
                        for (Order order : orderList) {
                            if (order.getOrderID().equals(orderId)) {
                                sendNotification(order.getCustomer().getCustomerID(),"Vendor " + this.vendorID +", "+vendorName+" cancelled the order with ID " + orderId);
                            }
                        }
                        //cancels unaccepted pending order
                            Order.updateOrderStatus(orderId,"Cancelled");
                        }
                    else{
                            System.out.println("Invalid Order ID");
                        }       
                }else{
                    System.out.println("Invalid input.");
                }
                vendorMenu(vendor);
                break;
            case 6:
                FileOperations.printPendingOrders(this.vendorID);
                vendorMenu(vendor);
                break;
            case 7:
                String orderHistoryVendorID = vendor.getUsername();
                vendor.orderHistory(orderHistoryVendorID, vendor);
                break;
            case 8:
                String vendorID8 = vendor.getUsername();
                System.out.println("Enter date (YYYY-MM-DD) for daily order history: ");
                String date8 = scanner.nextLine();
                vendor.displayDailyOrderHistory(vendorID8, date8,vendor);
                break;
            case 9:
                String vendorID9 = vendor.getUsername();
                System.out.println("Enter month (MM) for monthly order history: ");
                String month9 = scanner.nextLine();
                vendor.displayMonthlyOrderHistory(vendorID9, month9,vendor);
                break;
            case 10:
                String readReviewVendorID = vendor.getUsername();
                vendor.readReview(readReviewVendorID,vendor);
                break;
             case 11:
                String vendorID11 = vendor.getUsername();
                fop.viewTransactionHistory(vendorID11);
                vendorMenu(vendor);
                break;
             case 12:
                viewNotification(this.vendorID);
                    System.out.println("");
                    System.out.println("Would you like to remove a notification?");
                    System.out.println("1. Yes");
                    System.out.println("Enter any other key if No");
                    System.out.println("");
                    int notifchoice = scanner.nextInt();
                    if (notifchoice == 1){
                        System.out.println("Enter the notification number you wish to remove");
                        int removechoice = scanner.nextInt();
                        clearNotification(this.vendorID, removechoice);
                    }
                    vendorMenu(vendor);
                 break;
            case 0:
                User.displayLoginMenu();
                break;
            default:
                System.out.println("Invalid Choice");
                vendorMenu(vendor);
        }
    }
    
    public void createRestaurantItem(Vendor vendor) throws IOException {
        System.out.println("Enter Item Code: ");
        String itemCode = scanner.nextLine();
        System.out.println("Enter Item Name: ");
        String itemName = scanner.nextLine();
        System.out.println("Enter Price: ");
        double price = Double.parseDouble(scanner.nextLine());
        
        String vID = this.vendorID;
        String vname = this.vendorName;
        String icode = itemCode;
        String iname = itemName;
        double iprice = price;
        String iteminfo = vID + "," + vname + "," + icode + "," + iname + "," + iprice;
    
        fop.SaveItem(iteminfo, "Menu.txt");
        
        System.out.println("Do you want to create another item? [0-Yes/1-No]: ");
        int Choice = scanner.nextInt();
        
        switch (Choice) {
            case 0 -> createRestaurantItem(vendor);
            case 1 -> vendorMenu(vendor);
            default -> vendorMenu(vendor);
        }
    }
    
    public void displayItem(String displayVendor,Vendor vendor) throws IOException {
        fop.displayItem(displayVendor);
        vendorMenu(vendor);
    }
    
    public void updateItemPrice(String vendorName, String itemCode, Vendor vendor) throws IOException {
        fop.updateItemPrice(vendorName, itemCode);
        
        System.out.println("Do you want to update another item price? [0-Yes/1-No]: ");
        int Choice = scanner.nextInt();
        
        switch (Choice) {
            case 0 -> updateItemPrice(vendorName, itemCode, vendor);
            case 1 -> vendorMenu(vendor);
            default -> vendorMenu(vendor);
        }
    }
    
    public void deleteItem(String vendorName, String itemCode, Vendor vendor) throws IOException {
        fop.deleteItem(vendorName, itemCode);
        
        System.out.println("Do you want to delete another item? [0-Yes/1-No]: ");
        int Choice = scanner.nextInt();
        
        switch (Choice) {
            case 0 -> deleteItem(vendorName, itemCode, vendor);
            case 1 -> vendorMenu(vendor);
            default -> vendorMenu(vendor);
        }
    }
    
    public void orderHistory(String orderHistoryVendorID, Vendor vendor) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("OrderHistory.txt"));
        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[2].equalsIgnoreCase(orderHistoryVendorID)) {
                // Check if the vendor ID matches
                found = true;
                System.out.println("Order ID: " + parts[0]);
                System.out.println("Customer ID: " + parts[1]);
                System.out.println("Vendor ID: " + parts[2]);
                System.out.println("Order Status: " + parts[3]);
                System.out.println("Item Name: " + parts[4]);
                System.out.println("Date & Time: " + parts[5]);
                System.out.println("Price: " + parts[6]);
                System.out.println("Review: " + parts[7]);
                System.out.println("-------------------------------------");
            }
        }

        reader.close();

        if (!found) {
            System.out.println("No orders found for the given vendor ID.");
        }
        vendorMenu(vendor);
    }
    
    public void readReview(String readReviewVendorID, Vendor vendor) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("OrderHistory.txt"));
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3 && parts[2].equalsIgnoreCase(readReviewVendorID)) {
                    found = true;
                    System.out.println("Review of Cutomer ID " + "'" + parts[1] +  "'" + " for " + parts[4] + ": " + parts[7]);
                    System.out.println("---------------------------------------------------");
                }
            }

            if (!found) {
                System.out.println("No reviews found for this given vendor ID");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        vendorMenu(vendor);
    }
    
    public void displayDailyOrderHistory(String vendorID, String date,Vendor vendor) throws IOException {
        displayOrderHistoryByDate(vendorID, date,vendor);
    }
    
    public void displayMonthlyOrderHistory(String vendorID, String month, Vendor vendor) throws IOException {
        displayOrderHistoryByMonth(vendorID, month, vendor);
    }
    
    private void displayOrderHistoryByDate(String vendorID, String searchDate,Vendor vendor) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("OrderHistory.txt"));
        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[2].equalsIgnoreCase(vendorID)) {
                // Check if the vendor ID matches
                String[] dateTimeParts = parts[5].split("T");
                String orderDate = dateTimeParts[0]; // Extracting the date part

                // Check if the date matches based on searchDate (daily)
                if (searchDate.equals(orderDate)) {
                    found = true;
                    displayOrderDetails(parts);
                }
            }
        }

        reader.close();

        if (!found) {
            System.out.println("No orders found for the given vendor ID and date.");
        }
        
        System.out.println("Do you want to check another day order history? [0-Yes/1-No]: ");
        int Choice = scanner.nextInt();
        
        switch (Choice) {
            case 0 -> displayOrderHistoryByDate(vendorID, searchDate, vendor);
            case 1 -> vendorMenu(vendor);
            default -> vendorMenu(vendor);
        }
    }

    private void displayOrderHistoryByMonth(String vendorID, String searchMonth, Vendor vendor) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("OrderHistory.txt"));
        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 3 && parts[2].equalsIgnoreCase(vendorID)) {
                // Check if the vendor ID matches
                String[] dateTimeParts = parts[5].split("T");
                String orderDate = dateTimeParts[0]; // Extracting the date part
                String[] dateParts = orderDate.split("-");
                String orderMonth = dateParts[1]; // Extracting the month

                // Check if the month matches based on searchMonth (monthly)
                if (searchMonth.equals(orderMonth)) {
                    found = true;
                    displayOrderDetails(parts);
                }
            }
        }

        reader.close();

        if (!found) {
            System.out.println("No orders found for the given vendor ID and month.");
        }
        
        System.out.println("Do you want to check another month order history? [0-Yes/1-No]: ");
        int Choice = scanner.nextInt();
        
        switch (Choice) {
            case 0 -> displayOrderHistoryByMonth(vendorID, searchMonth, vendor);
            case 1 -> vendorMenu(vendor);
            default -> vendorMenu(vendor);
        }
    }
    
    private void displayOrderDetails(String[] parts) {
        System.out.println("Order ID: " + parts[0]);
        System.out.println("Customer ID: " + parts[1]);
        System.out.println("Vendor ID: " + parts[2]);
        System.out.println("Order Status: " + parts[3]);
        System.out.println("Item Name: " + parts[4]);
        System.out.println("Date & Time: " + parts[5]);
        System.out.println("Price: " + parts[6]);
        System.out.println("Review: " + parts[7]);
        System.out.println("-------------------------------------");
    
    }
    
    public static ArrayList<Vendor> readVendorsFromFile() {
    ArrayList<Vendor> vendorList = new ArrayList<Vendor>();

        try (BufferedReader br = new BufferedReader(new FileReader("VendorData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
 
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String vendorId = parts[0].trim();
                    String vendorPassword = parts[1].trim();
                    String vendorName = parts[2].trim();
                    String vendorRevenueString = parts[3].trim();
                    
                    double vendorRevenue = Double.parseDouble(vendorRevenueString);
                    
                    Vendor vendor = new Vendor(vendorId,vendorPassword,vendorName,vendorRevenue);
                    vendorList.add(vendor);
                } else {
                    // Handle invalid lines in the file
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vendorList;
    }
    
    
    public static void fileRevenueIncrease(double funds, String VendorID) {
    ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("VendorData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dataParts = line.split(",");
                if (dataParts.length == 4) {
                    String currentVendorId = dataParts[0].trim();
                    double currentRevenue = Double.parseDouble(dataParts[3].trim());
                
                    if (currentVendorId.equals(VendorID)) {
                        double updatedRevenue = currentRevenue + funds;
                        line = dataParts[0] + "," + dataParts[1] + "," + dataParts[2] + "," + updatedRevenue;
                    }
                }
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }

        // Write the updated lines back to the file
        try (FileWriter writer = new FileWriter("VendorData.txt")) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }  
}
   
    class Item {
        private String itemName;
        private String itemCode;
        private double price;

        public Item(String itemName, String itemCode, double price) {
            this.itemName = itemName;
            this.itemCode = itemCode;
            this.price = price;
        }

        public String getItemName() {
            return itemName;
        }

        public String getItemCode() {
            return itemCode;
        }

        public double getPrice() {
            return price;
        }
    
        public void setPrice(double price) {
            this.price = price;
        }
    }

    class Restaurant {
        private String name;
        private Set<Item> menu;

        public Restaurant(String name) {
            this.name = name;
            this.menu = new HashSet<>();
        }

        public void addItem(Item item) {
            menu.add(item);
        }

        public String getName() {
            return name;
        }

        public Set<Item> getMenu() {
            return menu;
        }   
}