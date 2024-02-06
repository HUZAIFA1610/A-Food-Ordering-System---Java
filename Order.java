
package com.mycompany.oodjassignment;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.time.format.DateTimeFormatter;

public class Order {
    private String OrderID;
    private Vendor VendorID;
    private Customer CustomerID;
    private String Status;
    private String Review;
    private LocalDateTime OrderDateTime;
    private double TotalPrice;
    private String Items;
    private boolean createdFromFile;
    private String DeliveryType;
    
    private FileOperations fop;
    
    //order constructor
    public Order(String OrderID, Customer CustomerID, Vendor VendorID, String Status, String Items, LocalDateTime OrderDateTime, double TotalPrice, String Review, boolean createdFromFile, String DeliveryType){
        this.OrderID = OrderID;
        this.VendorID = VendorID;
        this.CustomerID = CustomerID;
        this.Status = Status;
        this.Review = Review;
        this.OrderDateTime = OrderDateTime;
        this.TotalPrice = TotalPrice;
        this.Items = Items;
        this.DeliveryType = DeliveryType;
        
        this.fop = new FileOperations();
        
        //save order info to orderhistory when order is placed
        if(!createdFromFile){savedata();}
        
    }
    
    //GETTERS:
    public String getOrderID(){
        return OrderID;
    }
    
    public Vendor getVendor(){
        return VendorID;
    }
    
    public Customer getCustomer(){
        return CustomerID;
    }
    
    public String getReview(){
        return Review;
    }
    
    public String getStatus(){
        return Status;
    }
    
    public double getTotalPrice(){
        return TotalPrice;
    }
    
    public String getItems(){
        return Items;
    }
    
    public LocalDateTime getOrderDateTime(){
        return OrderDateTime;
    }
    
    public String getDeliveryType(){
        return DeliveryType;
    }
    
    
    //SETTERS:
    public void setStatus(String status){
        Status = status;
    }
    
    public void setReview(String review){
        Review = review;
    }
    
    
    //Check Status
    public static String CheckStatus(String orderId){
        ArrayList<Order> OrderList = readOrdersFromFile();
        for(Order order : OrderList){
            if (order.getOrderID().equalsIgnoreCase(orderId)) {
                return order.getStatus();
            }
        }
        return "Order ID not found";
    }
    

    
    //Changing status
    public static void updateOrderStatus(String orderId,String status) {
        ArrayList<Order> orderList = readOrdersFromFile();

        // Update the order status
        boolean orderUpdated = false;
        for (Order order : orderList) {
            if (order.getOrderID().equalsIgnoreCase(orderId)) {
                order.setStatus(status);
                orderUpdated = true;
                break; 
            }
        }

        // If the order was found and updated, write the modified content back to the file
        if (orderUpdated) {
            writeOrdersToFile(orderList);
            System.out.println("Order status updated successfully.");
        } else {
            System.out.println("Order not found with ID: " + orderId);
        }
    }
    
    
    //Review reader
    public static void readReview(String vendor){
        boolean flag = true;
        ArrayList<Order> OrderList = readOrdersFromFile();
        for(Order order : OrderList){
            if (vendor.equalsIgnoreCase(order.getVendor().getVendorID())) {
                System.out.println(order.getReview());
                flag = false;
            }
        }
        if (flag){
            System.out.println("No reviews for vendor " + vendor);
        }
        
    }
    
    
    //Adding order review
    public static void WriteReview(String orderId, String newReview) {
        ArrayList<Order> orderList = readOrdersFromFile();

        // Update the order review
        boolean orderUpdated = false;
        for (Order order : orderList) {
            if (order.getOrderID().equalsIgnoreCase(orderId)) {
                order.setReview(newReview);
                orderUpdated = true;
                break; // No need to continue searching
            }
        }

        // If the order was found and updated, write the modified content back to the file
        if (orderUpdated) {
            writeOrdersToFile(orderList);
            System.out.println("Order review added successfully.");
        } else {
            System.out.println("Order not found with ID: " + orderId);
        }
    }

    
    

    //Will generate unique OrderID and the OrderDateTime if order object is manually made by customer
    //If order object is made by text file, then it will retain the OrderID and OrderDateTime as per the text file in the object
    @Override
    public String toString() {
        
    if (createdFromFile){
        return OrderID + "," + CustomerID.getCustomerID() +"," + VendorID.getVendorID() +"," + Status +"," + Items +"," + OrderDateTime +"," + TotalPrice +","+Review+","+DeliveryType;
    } else{
        String OrderID = generateUniqueOrderID();
        LocalDateTime OrderDateTime = LocalDateTime.now();
        return OrderID + "," + CustomerID.getCustomerID() +"," + VendorID.getVendorID() +"," + Status +"," + Items +"," + OrderDateTime +"," + TotalPrice +","+Review+","+DeliveryType;
    }

    }
    
    //Saving data to Order History
    public void savedata(){
        fop.Write2File(this, "OrderHistory.txt");
    }
    
    
    
    
    //Loads all OrderID in OrderHistory.txt file into an ArrayList called existingOrderIDs
    private ArrayList<String> loadExistingOrderIDs() {
        ArrayList<String> existingOrderIDs = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader("OrderHistory.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    existingOrderIDs.add(parts[0]); 
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); 
        }

        return existingOrderIDs;
    }
    //Generates a random OrderID in ORD{number} format
    private String generateRandomOrderID() {
        int randomNum = new Random().nextInt(90000) + 10000;  //+10000 so it will always be a five digit number
        return String.format("ORD%d", randomNum);
    }
    private String generateUniqueOrderID() {
        ArrayList<String> existingOrderIDs = loadExistingOrderIDs();

        String newOrderID;
        // OrderID is PK so cannot have duplicate values
        do {
            newOrderID = generateRandomOrderID();
        } while (existingOrderIDs.contains(newOrderID));

        return newOrderID;
    }
    
 
    
    
    
    //Stores OrderHistory.txt contents into an arraylist of type Order and returns it
    public static ArrayList<Order> readOrdersFromFile() {
        ArrayList<Order> OrderList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("OrderHistory.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 9) {
                    String orderId = parts[0].trim();
                    String customerIdString = parts[1].trim();
                    String vendorIdString = parts[2].trim();
                    String status = parts[3].trim();
                    String items = parts[4].trim();
                    String datetimeString = parts[5].trim();
                    String totalpriceString = parts[6].trim();
                    String review = parts[7].trim();
                    String dType = parts[8].trim();
                    
                    //formatting from string to corresponding type
                    Customer customer = convertCustomerId(customerIdString);
                    Vendor vendor = convertVendorId(vendorIdString);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
                    LocalDateTime orderdatetime = LocalDateTime.parse(datetimeString, formatter);
                    double totalprice = Double.parseDouble(totalpriceString);

                    
                    Order order = new Order(orderId,customer,vendor,status,items,orderdatetime,totalprice,review,true,dType);
                    OrderList.add(order);
                }
            }
        } catch (IOException e) {
      
        }

        return OrderList;
    }
    
    

    
    //customer string to object converting method
    public static Customer convertCustomerId(String customerId){
        ArrayList<Customer> customerList = Customer.readCustomersFromFile();
        for (Customer customer : customerList) {
            if (customer.getCustomerID().equalsIgnoreCase(customerId)) {
                return customer;
            }
        }
        return null; 
    }
    
    
    
    //vendor string to object converting method
    public static Vendor convertVendorId(String vendorId){
        ArrayList<Vendor> vendorList = Vendor.readVendorsFromFile();
        for (Vendor vendor : vendorList) {
            if (vendor.getVendorID().equalsIgnoreCase(vendorId)) {
                return vendor;
            }
        }
        return null; 
    }
    public static Vendor convertVendorName(String vendorName){
        ArrayList<Vendor> vendorList = Vendor.readVendorsFromFile();
        for (Vendor vendor : vendorList) {
            if (vendor.getVendorName().equalsIgnoreCase(vendorName)) {
                return vendor;
            }
        }
        return null; 
    }
    
    
    //overwriting function to update order details after changes
     private static void writeOrdersToFile(ArrayList<Order> orderList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("OrderHistory.txt"))) {
            for (Order order : orderList) {
                // Write each order's details to the file
                bw.write(order.toCsvString());
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }
     
     public String toCsvString() {
     return OrderID + "," + CustomerID.getCustomerID() +"," + VendorID.getVendorID() +"," + Status +"," + Items +"," + OrderDateTime +"," + TotalPrice +","+Review+","+DeliveryType;
     }


    
    
    //Prints all order IDs of that customer
    public static void customersOrders(Customer customer){
        ArrayList<Order> orderList = Order.readOrdersFromFile();
            for (Order order : orderList) {
                if (order.getCustomer().getCustomerID().equals(customer.getCustomerID())) {
                    System.out.println(order.getOrderID());
                }
            }
    }
    
    
    //Prints order history of that customer
    public static void customersOrderHistory(Customer customer){
        ArrayList<Order> orderList = Order.readOrdersFromFile();
        for (Order order : orderList){
            if(order.getCustomer().getCustomerID().equals(customer.getCustomerID())) {
                
                System.out.println("Order ID: " + order.getOrderID() + "    Vendor ID: " + order.getVendor().getVendorID() + "    Status: " + order.getStatus() + "   Date and Time: " + order.getOrderDateTime() + "     Total Price: " + order.getTotalPrice() + "    Review: " + order.getReview()+"    Type: "+order.getDeliveryType());
                System.out.println("");
            }
        }
    }
    
   
    
}