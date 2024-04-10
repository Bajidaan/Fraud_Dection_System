package jidev.main;

import jidev.model.Transaction;
import jidev.service.FraudDetectionService;

import java.util.List;

public class FraudDetectionSystem {
    public static void main(String[] args) {
        FraudDetectionService fraudDetection = new FraudDetectionService();

        // Example transactions that would be streamed in real-time
        List<Transaction> transactions = List.of(
                new Transaction("2024-03-12 15:24", 150.00, "user1", "serviceA"),
                new Transaction("2024-03-12 20:24", 45000.00, "user2", "serviceB"),
                new Transaction("2024-03-12 15:24", 200, "user1", "serviceA")

        );

        for (Transaction transaction : transactions) {
            fraudDetection.processTransaction(transaction);
        }

    }
}
