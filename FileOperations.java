package com.mycompany.oodjassignment;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.*;
import java.util.Scanner;


public class FileOperations {
    private static ArrayList<Object>ClassList = new ArrayList<Object>();
    
    public static void printCustomerList(){
        System.out.println(ClassList);
    }
    
    public void Write2File(Object o, String fname){
        //Coding to write any class data to txt file
        
        ClassList.add(o);
        
        try{
            File outFile = new File(fname);
            FileWriter fileWriter = new FileWriter(outFile,true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(o);
            
            //System.out.println("Data written to the file");
            printWriter.close();
        }catch(IOException ex){
            System.out.println("Exception 1");
        }
        
            
        }
    public static void ReadFromFile(String filename){
            try{
                FileReader fileReader = new FileReader(filename);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                
                while((line = bufferedReader.readLine())!= null){
                    System.out.println(line);
                }
                 
                bufferedReader.close();
            }catch(IOException ex){
                System.out.println("Read Error");
            }
    }
    
    public static void SearchFromMenu(String fname, String VendorId) {
    try {
        FileReader fileReader = new FileReader(fname);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        int flag = 0;

        while ((line = bufferedReader.readLine()) != null) {
            String[] dataParts = line.split(",");
            if (dataParts.length >= 4) {
                String myVendorId = dataParts[0].trim();
                if (myVendorId.equalsIgnoreCase(VendorId)) {
                    //System.out.println("Data Found in the Text File");
                    System.out.println("Item ID: "+ dataParts[2] + ", Item Name: " + dataParts[3]+", Price: RM"+dataParts[4]);
                    flag = 1;
                }
            }
        }
        
        if (flag == 0) {
            System.out.println("Vendor ID '" + VendorId + "' not found");
        }
        
        bufferedReader.close();
        fileReader.close();
        } catch (IOException ex) {
            System.out.println("Search error");
            }   
    }
    
    
    public ArrayList<String> getCustomerOrderHistory(Customer customer) {
        ArrayList<String> customerOrderHistory = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("OrderHistory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(customer.getCustomerID())) {
                    customerOrderHistory.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }

        return customerOrderHistory;
    }
    
    
    
    //print list of vendor names for customer to choose from (from menu file)
    private static ArrayList<String> getVendorNames() {
        ArrayList<String> uniqueVendorId = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("Menu.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] dataParts = line.split(",");
                if (dataParts.length >= 2 && !uniqueVendorId.contains("Vendor ID: " + dataParts[0].trim() + ", Vendor Name: " +dataParts[1].trim())) {
                    uniqueVendorId.add("Vendor ID: " + dataParts[0].trim() + ", Vendor Name: " +dataParts[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uniqueVendorId;
    }
    public static void VendorNamesList(){
        ArrayList<String> uniqueVendorNames = getVendorNames();
        for (int line = 0; line < uniqueVendorNames.size(); line++) {
        System.out.println(uniqueVendorNames.get(line));
        }
    }
    
    
 
    
    //Login
    public static boolean login(String fname,String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userInfo = line.split(",");
                if (userInfo.length >= 2 && userInfo[0].equals(username) && userInfo[1].equals(password)) {
                    return true; // Login successful
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }
        return false; // Login failed
    }
    
    
    
    //Checking if item ID already exists
    public static boolean itemIdExists(String inputItemId) {
    try {
        FileReader fileReader = new FileReader("Menu.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        

        while ((line = bufferedReader.readLine()) != null) {
            String[] dataParts = line.split(",");
            if (dataParts.length >= 3) {
                String ItemID = dataParts[2].trim();
                if (ItemID.equalsIgnoreCase(inputItemId)){
                    return true;
                } 
            }
        }
            }catch (IOException e) {
            e.printStackTrace(); 
       }
        return false;
    }
    
    
    //Checking if Vendor ID already exists
    public static boolean vendorIdExists(String inputVendorId) {
    try {
        FileReader fileReader = new FileReader("Menu.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        

        while ((line = bufferedReader.readLine()) != null) {
            String[] dataParts = line.split(",");
            if (dataParts.length >= 3) {
                String VendorId = dataParts[0].trim();
                if (VendorId.equalsIgnoreCase(inputVendorId)){
                    return true;
                } 
            }
        }
            }catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
       }
        return false;
    }
    
    //getting Item price
    public static double getItemPrice(String inputItemId){
        try {
        FileReader fileReader = new FileReader("Menu.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        

        while ((line = bufferedReader.readLine()) != null) {
            String[] dataParts = line.split(",");
            if (dataParts.length >= 4) {
                String ItemID = dataParts[2].trim();
                String ItemPriceString = dataParts[4].trim();
                
                double ItemPrice = Double.parseDouble(ItemPriceString);
                
                if (ItemID.equalsIgnoreCase(inputItemId)){
                    return ItemPrice;
                } 
            }
        }
            }catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your needs
       }
        return 0;
    }
    
    //printing all pending orders of one customer
    //printing all accepted orders of one customer
    public static void printPendingOrders(String ID) {
    ArrayList<Order> orderList = Order.readOrdersFromFile();
    boolean foundUser = false;
    
    System.out.println("Pending orders :");

    for (Order order : orderList) {
        if (order.getCustomer().getCustomerID().equals(ID)) {
            foundUser = true;

            // Check if the order has "Pending" status for customer
            if (order.getStatus().equals("Pending")) {
                System.out.println("Order ID: " + order.getOrderID() + "       "+ (order.getItems()).replace(';', ','));
            }
        }
        else if(order.getVendor().getUsername().equals(ID)){
            foundUser = true;
            
            // Check if the order has "Pending" status for vendor
            if (order.getStatus().equals("Pending")) {
                System.out.println("Order ID: " + order.getOrderID() + "       "+ (order.getItems()).replace(';', ','));
            }
        }
    }
    
    

    if (!foundUser) {
        System.out.println("User not found in orders.");
    } 
    }
    
    //printing all accepted orders of one customer
    public static void printAcceptedOrders(String ID) {
    ArrayList<Order> orderList = Order.readOrdersFromFile();
    boolean foundUser = false;
    
    System.out.println("Accepted orders :");

    for (Order order : orderList) {
        if (order.getCustomer().getCustomerID().equals(ID)) {
            foundUser = true;

            // Check if the order has "Pending" status for customer
            if (order.getStatus().equals("Accepted")) {
                foundUser = true;
                
                System.out.println("Order ID: " + order.getOrderID() + "       "+ (order.getItems()).replace(';', ','));
            }
        }
        else if(order.getVendor().getUsername().equals(ID)){
            // Check if the order has "Pending" status for vendor
            if (order.getStatus().equals("Accepted")) {
                System.out.println("Order ID: " + order.getOrderID() + "       "+ (order.getItems()).replace(';', ','));
            }
        }
    }
    
    if (!foundUser) {
        System.out.println("User not found in orders.");
    } 
    }
    
    
    //Refund customer
    public void refundCustomer(String orderId) {
    ArrayList<String> lines = new ArrayList<>();
    ArrayList<Order> orderList = Order.readOrdersFromFile();
    for(Order order : orderList){
        if (orderId.equals(order.getOrderID())){
            String customerId = order.getCustomer().getCustomerID();
            try (BufferedReader br = new BufferedReader(new FileReader("CustomerData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dataParts = line.split(",");
                if (dataParts.length == 4) {
                    String currentCustomerId = dataParts[0].trim();
                    double currentWallet = Double.parseDouble(dataParts[3].trim());

                    if (currentCustomerId.equalsIgnoreCase(customerId)) {
                        double updatedWallet = currentWallet + order.getTotalPrice();
                        line = dataParts[0] + "," + dataParts[1] + "," + dataParts[2] + "," + updatedWallet;
                        
                        transactionHistory(customerId,"+" + order.getTotalPrice(), updatedWallet);
                }
            }
            lines.add(line);
        }

    } catch (IOException e) {
        e.printStackTrace(); 
    }
        }
    }

    

    // Write the updated lines back to the file
    try (FileWriter writer = new FileWriter("CustomerData.txt")) {
        for (String line : lines) {
            writer.write(line + System.lineSeparator());
        }
    } catch (IOException e) {
        e.printStackTrace(); 
    }
}
    
    //Refund vendor
    public void refundVendor(String orderId) {
    ArrayList<String> lines = new ArrayList<>();
    ArrayList<Order> orderList = Order.readOrdersFromFile();
    for(Order order : orderList){
        if (orderId.equals(order.getOrderID())){
            String vendorId = order.getVendor().getVendorID();
            try (BufferedReader br = new BufferedReader(new FileReader("VendorData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dataParts = line.split(",");
                if (dataParts.length == 4) {
                    String currentVendorId = dataParts[0].trim();
                    double currentRevenue = Double.parseDouble(dataParts[3].trim());

                    if (currentVendorId.equalsIgnoreCase(vendorId)) {
                        double updatedWallet = currentRevenue - order.getTotalPrice();
                        line = dataParts[0] + "," + dataParts[1] + "," + dataParts[2] + "," + updatedWallet;
                        
                        transactionHistory(vendorId,"-" + order.getTotalPrice(), updatedWallet);
                }
            }
            lines.add(line);
        }

    } catch (IOException e) {
        e.printStackTrace(); 
    }
        }
    }

    

    // Write the updated lines back to the file
    try (FileWriter writer = new FileWriter("VendorData.txt")) {
        for (String line : lines) {
            writer.write(line + System.lineSeparator());
        }
    } catch (IOException e) {
        e.printStackTrace(); 
    }
}
    
    
    public void paymentCustomer(String orderId) {
    ArrayList<String> lines = new ArrayList<>();
    ArrayList<Order> orderList = Order.readOrdersFromFile();
    for(Order order : orderList){
        if (orderId.equals(order.getOrderID())){
            String customerId = order.getCustomer().getCustomerID();
            try (BufferedReader br = new BufferedReader(new FileReader("CustomerData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dataParts = line.split(",");
                if (dataParts.length == 4) {
                    String currentCustomerId = dataParts[0].trim();
                    double currentWallet = Double.parseDouble(dataParts[3].trim());

                    if (currentCustomerId.equalsIgnoreCase(customerId)) {
                        double updatedWallet = currentWallet - order.getTotalPrice();
                        line = dataParts[0] + "," + dataParts[1] + "," + dataParts[2] + "," + updatedWallet;
                        
                        transactionHistory(customerId,"-" + order.getTotalPrice(), updatedWallet);
                }
            }
            lines.add(line);
        }

    } catch (IOException e) {
        e.printStackTrace(); 
    }
        }
    }

    

    // Write the updated lines back to the file
    try (FileWriter writer = new FileWriter("CustomerData.txt")) {
        for (String line : lines) {
            writer.write(line + System.lineSeparator());
        }
    } catch (IOException e) {
        e.printStackTrace(); 
    }
}
    
    //Payment received by vendor
    public void paymentVendor(String orderId) {
    ArrayList<String> lines = new ArrayList<>();
    ArrayList<Order> orderList = Order.readOrdersFromFile();
    for(Order order : orderList){
        if (orderId.equals(order.getOrderID())){
            String vendorId = order.getVendor().getVendorID();
            try (BufferedReader br = new BufferedReader(new FileReader("VendorData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] dataParts = line.split(",");
                if (dataParts.length == 4) {
                    String currentVendorId = dataParts[0].trim();
                    double currentRevenue = Double.parseDouble(dataParts[3].trim());

                    if (currentVendorId.equalsIgnoreCase(vendorId)) {
                        double updatedWallet = currentRevenue + order.getTotalPrice();
                        line = dataParts[0] + "," + dataParts[1] + "," + dataParts[2] + "," + updatedWallet;
                        
                        transactionHistory(vendorId,"+" + order.getTotalPrice(), updatedWallet);
                }
            }
            lines.add(line);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
        }
    }

    

    // Write the updated lines back to the file
    try (FileWriter writer = new FileWriter("VendorData.txt")) {
        for (String line : lines) {
            writer.write(line + System.lineSeparator());
        }
    } catch (IOException e) {
        e.printStackTrace(); 
    }
}
    
    
    
    
    
    //Transaction history updating
    public void transactionHistory(String ID, String amount, double updatedWallet){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("TransactionHistory.txt", true))) {
            writer.write(ID + "," + amount + "," + updatedWallet);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Transaction history viewing
    public void viewTransactionHistory(String ID){
        boolean found = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("TransactionHistory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].trim().equals(ID)) {
                    found = true;
                    System.out.println("ID: " + parts[0] +"   Transaction: " + parts[1] + "   Amount after transaction: " + parts[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading from TransactionHistory.txt: " + e.getMessage());
        }
        if(!found){
            System.out.println("Transaction history empty for user " + ID);
        }
    }
    
   //Save new item
   public void SaveItem(String iteminfo, String fname) {
        try {
            File outFile = new File(fname);
            FileWriter fileWriter = new FileWriter(outFile, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            // Assuming 'o' has a proper toString() method
            printWriter.println(iteminfo);

            System.out.println("Item Created Successfully");
            printWriter.close();
        } catch (IOException ex) {
            System.out.println("Exception while writing to file");
            ex.printStackTrace();
        }
    }
    
   //Display items of vendor
    public void displayItem(String displayVendor){
        try{
            BufferedReader reader = new BufferedReader(new FileReader("Menu.txt"));
            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equalsIgnoreCase(displayVendor)) {
                    found = true;
                    System.out.println("Vendor ID: " + parts[0]);
                    System.out.println("Vendor Name: " + parts[1]);
                    System.out.println("Item Code: " + parts[2]);
                    System.out.println("Item Name: " + parts[3]);
                    System.out.println("Price: " + parts[4]);
                    System.out.println("-------------------------------------");
                }
            }

            reader.close();

            if (!found) {
                System.out.println("Vendor not found or no items available for this Vendor.");
            }
        } catch (IOException ex) {
            System.out.println("Exception while writing to file");
            ex.printStackTrace();
        }   
    }
    
    //Update price of item
    public void updateItemPrice(String vendorName, String itemCode) throws IOException {
        try{
            File inputFile = new File("Menu.txt");
            File tempFile = new File("temp_menu_data.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String line;
            boolean found = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5 && parts[1].equalsIgnoreCase(vendorName) && parts[2].equalsIgnoreCase(itemCode)) {
                    found = true;
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Enter new price for " + parts[3] + ": ");
                    double newPrice = Double.parseDouble(scanner.nextLine());
                
                    parts[4] = String.valueOf(newPrice);

                    writer.write(String.join(",", parts));
                    writer.newLine();
                    System.out.println("Price updated successfully.");
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
            reader.close();
            writer.close();

            if (inputFile.delete()) {
                if (!tempFile.renameTo(inputFile)) {
                    System.out.println("Could not rename the temporary file.");
                }
            } else {
                System.out.println("Could not delete the original file.");
            }

            if (!found) {
                System.out.println("Item not found for the given restaurant and item code.");    
            }
        } catch (IOException ex) {
            System.out.println("Exception while writing to file");
            ex.printStackTrace();
        }
    }
    

    
    //Delete an item
    public void deleteItem(String vendorName, String itemCode) throws IOException {
        try{
            File inputFile = new File("Menu.txt");
        File tempFile = new File("temp_menu_data.txt");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String line;
        boolean found = false;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5 && parts[1].equalsIgnoreCase(vendorName) && parts[2].equalsIgnoreCase(itemCode)) {
                found = true;
                System.out.println("Item found and deleted: " + parts[3]);
            } else {
                writer.write(line);
                writer.newLine();
            }
        }

        reader.close();
        writer.close();
        
        if (inputFile.delete()) {
            if (!tempFile.renameTo(inputFile)) {
                System.out.println("Could not rename the temporary file.");
            }
        } else {
            System.out.println("Could not delete the original file.");
        }

        if (!found) {
            System.out.println("Item not found for the given restaurant and item code.");
        }
        } catch (IOException ex) {
            System.out.println("Exception while writing to file");
            ex.printStackTrace();
        }
    }
} 
    

        
    