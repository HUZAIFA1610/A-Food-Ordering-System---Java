
package com.mycompany.oodjassignment;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.text.StyledEditorKit.ForegroundAction;

import java.io.File;

public class Delivery {

    private ArrayList<Runner> runners = initializeRunners("Runners.txt");;
    private Scanner sc;
    
  
    public void deliveryScreen(String runnerId) {
        mainMenu: while (true) {
            String choice = "A";
            System.out.print("""
                    [A] View Tasks
                    [B] Update Task Statuses
                    [C] View Task History
                    [D] Revenue Dashboard

                    Select:  """);
            choice = stringInput();

            // View the available tasks and let the runner choose to accept or decline them.
            // if runner declines, call assignRunner again
            if (choice.equalsIgnoreCase("A")) {
                File file = new File("OrderHistory.txt");
                if (file.length() == 0) {
                    System.out.println("There are no more orders to accept.");
                }

                // INSERT CODE THAT SORTS OUT ALL THE NON-DELIVERY ORDERS HERE
                for (int i = 0; i < runners.size(); i++) {
                    if (runners.get(i).id.equalsIgnoreCase(runnerId)) {
                        // checks if there are no assigned tasks
                        if (runners.get(i).assigned.isEmpty()) {
                            System.out.println("There are no assigned tasks to update");
                        }
                        // displays assigned tasks to accept or decline
                        else {
                            for (int j = 0; j < runners.get(i).assigned.size(); j++) {
                                System.out.printf("[%d] %s\n", j + 1, runners.get(i).assigned.get(j));
                            }
                        }

                        // code to go back to main menu
                        System.out.println("\n[A] Main Menu\n");
                        selection: while (true) {
                            System.out.print("Select: ");
                            choice = stringInput();

                            // ensures user adheres to available options
                            if (!isInt(choice)) {
                                if (choice.equalsIgnoreCase("A")) {
                                    continue mainMenu;
                                } else {
                                    continue selection;
                                }
                            } else if (Integer.parseInt(choice) > runners.get(i).assigned.size()
                                    || Integer.parseInt(choice) < 1) {
                                continue selection;
                            }
                            String accept;
                            do {
                                System.out.printf("Accept order %s? (Y to accept / N to decline / B to go back): ",
                                        runners.get(i).accepted.get(Integer.parseInt(choice) - 1));
                                accept = stringInput();
                            } while (!(accept.equalsIgnoreCase("Y") || accept.equalsIgnoreCase("N")
                                    || accept.equalsIgnoreCase("Yes") || accept.equalsIgnoreCase("No")
                                    || accept.equalsIgnoreCase("Back") || accept.equalsIgnoreCase("B")));

                            if (accept.equalsIgnoreCase("Y") || accept.equalsIgnoreCase("Yes")) {
                                // Moves order to the accepted column
                                runners.get(i).accepted.add(runners.get(i).assigned.get(Integer.parseInt(choice) - 1));
                                runners.get(i).assigned.remove(Integer.parseInt(choice) - 1);
                                updateRunners(runners);
                                System.out.println("Delivery order accepted!");

                                System.out.println();
                                continue mainMenu;

                            } else if (accept.equalsIgnoreCase("N") || accept.equalsIgnoreCase("No")) {
                                // Moves order to the declined column
                                runners.get(i).declined.add(runners.get(i).assigned.get(Integer.parseInt(choice) - 1));
                                runners.get(i).assigned.remove(Integer.parseInt(choice) - 1);
                                updateRunners(runners);
                                System.out.println("Delivery order declined!");

                                System.out.println();
                                continue mainMenu;

                            } else {
                                continue selection;
                            }
                        }
                    }
                }
            }

            // View accepted tasks and declare if task is completed or not. If completed,
            // change the task order status from pending to complete and add revenue to
            // runner
            else if (choice.equalsIgnoreCase("B")) {
                for (int i = 0; i < runners.size(); i++) {
                    if (runners.get(i).id.equalsIgnoreCase(runnerId)) {
                        // checks if there are no accepted tasks
                        if (runners.get(i).accepted.isEmpty()) {
                            System.out.println("There are no accepted tasks to update");
                        }
                        // displays accepted tasks to update
                        else {
                            for (int j = 0; j < runners.get(i).accepted.size(); j++) {
                                System.out.printf("[%d] %s\n", j + 1, runners.get(i).accepted.get(j));
                            }
                        }

                        // code to go back to main menu
                        System.out.println("\n[A] Main Menu\n");
                        selection: while (true) {
                            System.out.print("Select: ");
                            choice = stringInput();

                            // ensures user adheres to available options
                            if (!isInt(choice)) {
                                if (choice.equalsIgnoreCase("A")) {
                                    continue mainMenu;
                                } else {
                                    continue selection;
                                }
                            } else if (Integer.parseInt(choice) > runners.get(i).accepted.size()
                                    || Integer.parseInt(choice) < 1) {
                                continue selection;
                            }
                            String complete;
                            do {
                                System.out.printf("Mark order %s as complete? (Y/N): ",
                                        runners.get(i).accepted.get(Integer.parseInt(choice) - 1));
                                complete = stringInput();
                            } while (!(complete.equalsIgnoreCase("Y") || complete.equalsIgnoreCase("N")
                                    || complete.equalsIgnoreCase("Yes") || complete.equalsIgnoreCase("No")));

                            if (complete.equalsIgnoreCase("Y") || complete.equalsIgnoreCase("Yes")) {
                                // Updates order status and runner balance. Removes order from accepted list and
                                // adds it to completed list
                                Order.updateOrderStatus(runners.get(i).accepted.get(Integer.parseInt(choice) - 1),
                                        "Complete");
                                runners.get(i).balance += 5.5;
                                runners.get(i).completed.add(runners.get(i).accepted.get(Integer.parseInt(choice) - 1));
                                runners.get(i).accepted.remove(Integer.parseInt(choice) - 1);
                                updateRunners(runners);
                                System.out.println("Delivery order completed!");

                                System.out.println();
                                continue mainMenu;
                            } else {
                                continue selection;
                            }
                        }
                    }
                }
            } else if (choice.equalsIgnoreCase("C")) {

            }
        }
    }

    private ArrayList<Runner> initializeRunners(String usersFilePath) {
        ArrayList<Runner> runners = new ArrayList<Runner>();
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(usersFilePath))) {

            while ((line = br.readLine()) != null) {

                // using comma as separator for the Runners.txt csv
                String[] user = line.split(",");

                // converting the string values of accepted and declined task codes to an
                // ArrayList
                ArrayList<String> assigned = new ArrayList<String>(Arrays.asList(user[4].split("\\|")));
                ArrayList<String> accepted = new ArrayList<String>(Arrays.asList(user[5].split("\\|")));
                ArrayList<String> declined = new ArrayList<String>(Arrays.asList(user[6].split("\\|")));
                ArrayList<String> completed = new ArrayList<String>(Arrays.asList(user[7].split("\\|")));

                // calling the constructor of the Runner class and adding the initialized runner
                // to an arraylist
                Runner runner = new Runner(user[0], user[1], user[2], Double.parseDouble(user[3]), assigned, accepted,
                        declined, completed);
                runners.add(runner);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return runners;
    }

    /*
     * This function is called in the main class when a customer orders choosing
     * delivery option
     * 
     * Returns true if runner is assigned
     * 
     */
    public boolean assignRunner(String orderId) {

        ArrayList<Runner> temp = runners;
        Random rd = new Random();

        while (!temp.isEmpty()) {
            int index = rd.nextInt(temp.size());

            // if the randomly selected runner has already declined the task, remove the
            // runner
            // from the search list and search again
            if (temp.get(index).declined.contains(orderId)) {
                temp.remove(index);
                continue;
            }

            // otherwise, the orderId is appended to the selected runner's "assigned tasks"
            // arraylist
            for (int i = 0; i < runners.size(); i++) {
                if (runners.get(i).id == temp.get(index).id) {
                    temp.get(index).assigned.add(orderId);
                    runners.set(i, temp.get(index));
                    updateRunners(runners);
                    return true;
                }
            }
        }
        return false;
    }

    // method to update the Runners.txt csv from an inputted Arraylist of Runners
    private void updateRunners(ArrayList<Runner> runnerList) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Runners.txt"))) {
            for (Runner runner : runnerList) {
                // Write each order's details to the file
                bw.write(runner.toCsvString());
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }

    // method to ensure user inputs integer
    private int intInput() {
        sc = new Scanner(System.in);
        try {
            int input = sc.nextInt();
            sc.nextLine();
            return input;
        } catch (InputMismatchException e) {
            System.out.print("Invalid Input. Please try again: ");
            sc.nextLine();
            return intInput();
        }
    }

    // method to ensure user inputs string
    private String stringInput() {
        sc = new Scanner(System.in);
        try {
            String input = sc.nextLine();
            return input;
        } catch (InputMismatchException e) {
            System.out.print("Invalid Input. Please try again: ");
            sc.nextLine();
            return stringInput();
        }
    }

    // method to check if a string is actually an integer
    private boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
