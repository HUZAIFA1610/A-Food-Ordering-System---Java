
package com.mycompany.oodjassignment;

import java.util.ArrayList;

public class Runner {
    public String id;
    public String name;
    public String password;
    public double balance;
    public ArrayList<String> assigned;
    public ArrayList<String> accepted;
    public ArrayList<String> declined;
    public ArrayList<String> completed;

    public Runner(String id, String password, String name, double balance, ArrayList<String> assigned,
            ArrayList<String> accepted,
            ArrayList<String> declined, ArrayList<String> completed) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.balance = balance;
        this.assigned = assigned;
        this.accepted = accepted;
        this.declined = declined;
        this.completed = completed;
    }

    public String toCsvString() {
        String assigned = "";
        String accepted = "";
        String declined = "";
        String completed = "";

        for (int i = 0; i < this.assigned.size(); i++) {
            if (i == this.assigned.size() - 1) {
                assigned += this.assigned.get(i);
            } else {
                assigned += this.assigned.get(i) + "|";
            }
        }

        for (int i = 0; i < this.accepted.size(); i++) {
            if (i == this.accepted.size() - 1) {
                accepted += this.accepted.get(i);
            } else {
                accepted += this.accepted.get(i) + "|";
            }
        }

        for (int i = 0; i < this.declined.size(); i++) {
            if (i == this.declined.size() - 1) {
                declined += this.declined.get(i);
            } else {
                declined += this.declined.get(i) + "|";
            }
        }

        for (int i = 0; i < this.completed.size(); i++) {
            if (i == this.completed.size() - 1) {
                completed += this.completed.get(i);
            } else {
                completed += this.completed.get(i) + "|";
            }
        }

        String str = String.format("%s,%s,%s,%f,%s,%s,%s,%s", this.id, this.password, this.name, this.balance, assigned,
                accepted, declined, completed);
        return str;
    }
}
