package jidev.repository;

import jidev.model.Transaction;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionRepository {
    private static final String TRANSACTION_FILE =
            "src/main/java/jidev/repository/transactions.txt";

    // Method to read transactions from the file and return a list of Transaction objects
    public static List<Transaction> readTransactionsFromFile() {
        List<Transaction> transactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(TRANSACTION_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String timestamp = parts[0].trim();
                double amount = Double.parseDouble(parts[1].trim());
                String userID = parts[2].trim();
                String serviceID = parts[3].trim();
                transactions.add(new Transaction(timestamp, amount, userID, serviceID));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Method to add a new transaction to the file
    public static void addTransaction(Transaction transaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
            String line = transaction.getTimestamp().format(formatter) + "," + transaction.getAmount() + "," +
                    transaction.getUserID() + "," + transaction.getServiceID();
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Map<String, List<Transaction>> getUserTransaction() {
        return readTransactionsFromFile().stream()
                .collect(Collectors.groupingBy(Transaction::getUserID));
    }

}
