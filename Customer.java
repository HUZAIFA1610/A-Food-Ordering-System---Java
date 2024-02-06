
package com.mycompany.oodjassignment;

//imports
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.util.Random;


//Class Customer
public class Customer extends User{
    private String CustomerName;
    private double wallet;
    private FileOperations fop;
    //Username from parent User class taken into variables CustomerID
    private String CustomerID = getUsername();
    
    
    //1.
    //Customer ID getter
    public String getCustomerID(){
        return CustomerID;
    }
    
    
    //2.
    //Customer constructor
    public Customer(String username, String password, String CustomerName, double wallet) {
        //username and password are from parent User class
        super(username,password);
        this.CustomerName = CustomerName;
        this.wallet = wallet;
        this.fop = new FileOperations();
     }
    
    
    //3.
    //Customer Menu
    public void displayCustomerMainMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Customer Main Menu:");
            System.out.println("1. View Menu");
            System.out.println("2. Read Vendor Reviews");
            System.out.println("3. Cancel Order");
            System.out.println("4. Check Order Status");
            System.out.println("5. Check/Re-order From Order History");
            System.out.println("6. Transaction History");
            System.out.println("7. Write Review");
            System.out.println("8. Order Recieved");
            System.out.println("9. Notifications");
            System.out.println("0. Log out");

            System.out.print("Enter your choice: ");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    fop.VendorNamesList();
                    System.out.println("Enter Vendor ID: ");
                    String vendorId = scanner.next();
                    if (fop.vendorIdExists(vendorId)){
                        viewMenuAndOrder(vendorId);
                    }
                    else{
                        System.out.println("Vendor ID '" + vendorId + "' does not exist.");
                    }
                    
                    break;
                case "2":
                    fop.VendorNamesList();
                    System.out.println("Enter Vendor ID: ");
                    String Vendorid = scanner.next();
                    if (fop.vendorIdExists(Vendorid)){
                        Order.readReview(Vendorid);
                    }
                    else{
                        System.out.println("Vendor ID '" + Vendorid + "' does not exist.");
                    }
                    displayCustomerMainMenu();
                    break;
                case "3":
                    fop.printAcceptedOrders(this.CustomerID);
                    fop.printPendingOrders(this.CustomerID);
                    System.out.println("Enter Order ID of order to cancel: ");
                    String orderId = scanner.next();
                    
                    ArrayList<Order> orderList = Order.readOrdersFromFile();
                    
                    if (Order.CheckStatus(orderId).equals("Accepted")){
                        //deduct from vendor revenue and refund customer
                        fop.refundVendor(orderId);
                        fop.refundCustomer(orderId);
                        //cancels accepted order
                        Order.updateOrderStatus(orderId,"Cancelled");
                        for (Order order : orderList) {
                            if (order.getOrderID().equals(orderId)) {
                                sendNotification(order.getVendor().getVendorID(),"Customer " + this.CustomerID +" cancelled the order with ID " + orderId);
                            }
                        }
                        System.out.println("Order cancelled successfully! Funds have been refunded to your wallet.");
                        }else if (Order.CheckStatus(orderId).equals("Pending")){
                        //cancels unaccepted pending order
                            Order.updateOrderStatus(orderId,"Cancelled");
                            for (Order order : orderList) {
                            if (order.getOrderID().equals(orderId)) {
                                sendNotification(order.getVendor().getVendorID(),"Customer " + this.CustomerID +" cancelled the order with ID " + orderId);
                            }
                        }
                        }else{
                            System.out.println("Invalid Order ID");
                        }
                    displayCustomerMainMenu();
                    break;
                case "4":
                    Order.customersOrders(this);
                    System.out.println("Enter Order ID: ");
                    String orderID = scanner.next();
                    System.out.println(Order.CheckStatus(orderID));
                    break;
                case "5":
                    Order.customersOrderHistory(this);
                    System.out.println("Would you like to reorder?");
                    System.out.println("1. Yes");
                    System.out.println("2. No");
                    String reorderchoice = scanner.next();
                    switch(reorderchoice){
                        case "1":
                            System.out.println("Enter Order ID: of the items you wish to reorder: ");
                            String ORDERID = scanner.next();
                            reorder(ORDERID);
                    }
                    displayCustomerMainMenu();
                    break;
                case "6":
                    fop.viewTransactionHistory(this.getCustomerID());
                    displayCustomerMainMenu();
                    break;
                case "7":
                    System.out.println("Customer's order history: ");
                    Order.customersOrderHistory(this);
                    System.out.println("Enter Order ID of order to review: ");
                    String ORDERid = scanner.next();
                    scanner.nextLine();
                    System.out.println("Write Review: ");
                    String review = scanner.nextLine();
                    Order.WriteReview(ORDERid, review);
                    displayCustomerMainMenu();
                    break;
                case "8":
                    fop.printAcceptedOrders(this.CustomerID);
                    ArrayList<Order> orderlist = Order.readOrdersFromFile();
                    System.out.println("Enter Order ID of order to mark as received: ");
                    String orderIDD = scanner.next();
                    if (Order.CheckStatus(orderIDD).equals("Accepted")){
                        //cancels unaccepted pending order
                            Order.updateOrderStatus(orderIDD,"Complete");
                            for (Order order : orderlist) {
                            if (order.getOrderID().equals(orderIDD)) {
                                sendNotification(order.getVendor().getVendorID(),"Customer " + this.CustomerID +" received the order with ID " + orderIDD);
                            }
                        }
                    }else{
                        System.out.println("Invalid Order ID.");
                    }
                    displayCustomerMainMenu();
                    break;
                case "9":
                    viewNotification(this.CustomerID);
                    System.out.println("");
                    System.out.println("Would you like to remove a notification?");
                    System.out.println("1. Yes");
                    System.out.println("Enter any other key if No");
                    System.out.println("");
                    int notifchoice = scanner.nextInt();
                    if (notifchoice == 1){
                        System.out.println("Enter the notification number you wish to remove");
                        int removechoice = scanner.nextInt();
                        clearNotification(this.CustomerID, removechoice);
                    }
                    displayCustomerMainMenu();
                    break;
                case "0":
                    System.out.println("Exiting the menu. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
    
    //4.
    //View menu and order function
    public void viewMenuAndOrder(String VendorId){
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> itemlist = new ArrayList<String>();

        double totalprice = 0;
        fop.SearchFromMenu("Menu.txt", VendorId);
        
        System.out.println("Would you like to order?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        String choice = scanner.next();
        while(choice.equals("1")){
            System.out.println("Enter item ID: ");
            String itemId = scanner.next();
            
            if (fop.itemIdExists(itemId)){
            itemlist.add(itemId);
            totalprice = totalprice + fop.getItemPrice(itemId);
            }else{
                System.out.println("Item ID not found.");
            }
            
            System.out.println("Order more?");
            System.out.println("1. Yes");
            System.out.println("2. No");
            String morechoice = scanner.next();
            if (morechoice.equals("2")){
                choice = "2";
                if (itemlist.isEmpty()){
                        System.out.println("No items in cart.");
                    }
                    else{
                        System.out.println("Total price: " + totalprice + ". Confirm order?");
                        System.out.println("1. Yes");
                        System.out.println("2. No");
                        String confirmorderchoice = scanner.next();
                        if(confirmorderchoice.equals("1")){
                            if (wallet >= totalprice){

                                String itemliststring = String.join(";", itemlist);
                                
                                Vendor vendorID = convertVendorId(VendorId);
                                System.out.println("Delivery or Dine-in?");
                                System.out.println("1. Delivery");
                                System.out.println("2. Dine-in");
                                String deliverychoice = scanner.next();
                                switch (deliverychoice){
                                    case "1":
                                            Random rd = new Random();
                                            int x = rd.nextInt(90000) + 10000;
                                            String orderIDDD = String.format("ORD%d", x);
                                            Delivery delivery = new Delivery();
                                            if(delivery.assignRunner(orderIDDD)){
                                                if(this.wallet >= totalprice+5.5){
                                                    double newwwallet = this.wallet-5.5;
                                                    this.wallet = newwwallet;
                                                    fop.transactionHistory(this.CustomerID, "-5.5",newwwallet);
                                                place_order(vendorID,itemliststring, totalprice,"Delivery");
                                                sendNotification(VendorId,"Customer with Customer ID "+this.CustomerID+" placed an order");
                                                }
                                        }
                                        else{
                                            System.out.println("No delivery runners available.");
                                        }
                                        break;
                                    case "2":
                                        place_order(vendorID,itemliststring, totalprice,"Dine-in");
                                        sendNotification(VendorId,"Customer with Customer ID "+this.CustomerID+" placed an order");
                                }
                            }
                            else{
                                System.out.println("Insufficient funds.");
                            }
                        }
                }
            }
        } 
    }
    
    
    //5.
    //Place order function to create a new order object
    public void place_order(Vendor vendor,String items, double totalprice, String deliveryType){  
        Order order = new Order("Order",this,vendor,"Pending",items,LocalDateTime.now(),totalprice,"null",false,deliveryType);
        System.out.println("Order placed!");
    }
    
    //6.
    //Check order history function
    public ArrayList<String> getOrderHistory() {
        ArrayList<String> customerOrderHistory = new ArrayList<>();
        customerOrderHistory = fop.getCustomerOrderHistory(this);
        return customerOrderHistory;
    }
    public void printOrderHistory() {
        ArrayList<String> orderHistory = getOrderHistory();
        if (orderHistory.isEmpty()) {
            System.out.println("No order history found for customer " + getCustomerID());
        } else {
            System.out.println("Order History for customer " + getCustomerID() + ":");
            for (String order : orderHistory) {
                System.out.println(order);
            }
        }
    }
    
    
    //7.
    //Re-order function
    public void reorder(String orderId){
        ArrayList<Order> orderList = Order.readOrdersFromFile();
        Scanner scanner = new Scanner(System.in);
        
        for(Order order : orderList){
            if (order.getOrderID().equals(orderId)){
                if(order.getTotalPrice() <= this.wallet){
                    System.out.println("Delivery or Dine-in?");
                    System.out.println("1. Delivery");
                    System.out.println("2. Dine-in");
                    String deliverychoice = scanner.next();
                    switch(deliverychoice){
                        case "1":
//                            if (Delivery.runnersAvailable()){
                            Random rd = new Random();
                                int x = rd.nextInt(90000) + 10000;
                                String orderIDDD = String.format("ORD%d", x);
                                Delivery delivery = new Delivery();
                                if(delivery.assignRunner(orderIDDD)){
                                    if (this.wallet >= order.getTotalPrice() + 5.5){
                                        double newwallet = this.wallet - 5.5;
                                        this.wallet = newwallet;
                                        fop.transactionHistory(this.CustomerID, "-5.5",newwallet);
                                        place_order(order.getVendor(),order.getItems(),order.getTotalPrice(),"Delivery");
                                        sendNotification(order.getVendor().getVendorID(),"Customer "+this.CustomerID+" placed an order");
                                    }else{
                                        System.out.println("Insufficient funds.");
                                    }
                            }else{
                                System.out.println("Delivery runners not available.");
                            }
                            break;
                        case "2":
                            place_order(order.getVendor(),order.getItems(),order.getTotalPrice(),"Dine-in");
                            sendNotification(order.getVendor().getVendorID(),"Customer "+this.CustomerID+" placed an order");
                            break;
                    }
                
                
                }else{
                    System.out.println("Insufficient funds.");
                }
            }    
        }  
    }
    
    
    //8.
    //Vendor string to object converting method
    public Vendor convertVendorId(String vendorId){
        ArrayList<Vendor> vendorList = Vendor.readVendorsFromFile();
        for (Vendor vendor : vendorList) {
            if (vendor.getVendorID().equalsIgnoreCase(vendorId)) {
                return vendor;
            }
        }
        return null; 
    }
    
    
    //9.
    //Creating arraylist for customer class from CustomerData.txt file
    public static ArrayList<Customer> readCustomersFromFile() {
        ArrayList<Customer> customerList = new ArrayList<Customer>();

        try (BufferedReader br = new BufferedReader(new FileReader("CustomerData.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming each line has vendor information separated by a comma
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String customerId = parts[0].trim();
                    String customerPassword = parts[1].trim();
                    String customerName = parts[2].trim();
                    String walletString = parts[3].trim();
                    
                    //converting wallet from String to double
                    double wallet = Double.parseDouble(walletString);
                    Customer customer = new Customer(customerId,customerPassword,customerName,wallet);
                    customerList.add(customer);
                } else {
                    // Handle invalid lines in the file
                    System.out.println("Skipping invalid line: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return customerList;
    }
    
   
    
}

