package jidev.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private LocalDateTime timestamp;
    private double amount;
    private String userID;
    private String serviceID;

    public Transaction(String timestamp, double amount, String userID, String serviceID) {
        this.timestamp = getTime(timestamp);
        this.amount = amount;
        this.userID = userID;
        this.serviceID = serviceID;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = getTime(timestamp);
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getServiceID() {
        return serviceID;
    }

    public void setServiceID(String serviceID) {
        this.serviceID = serviceID;
    }

    private LocalDateTime getTime(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "timestamp=" + timestamp +
                ", amount=" + amount +
                ", userID='" + userID + '\'' +
                ", serviceID='" + serviceID + '\'' +
                '}';
    }
}
