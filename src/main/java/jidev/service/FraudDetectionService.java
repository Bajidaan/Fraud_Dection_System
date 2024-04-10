package jidev.service;

import jidev.model.Transaction;
import jidev.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.*;

public class FraudDetectionService {

    private static final int MIN_DISTINCT_SERVICES = 3;
    private static final Map<String, List<Transaction>> pendingTransactions = new HashMap<>();


    public void processTransaction(Transaction transaction) {
        // Check for multi-service activity
        flagThreeDistinctService(transaction);

        // Check for high transaction amount
        flagHighTransactionAmount(transaction);

        // Check for ping-pong activity
        detectPingPong(TransactionRepository.readTransactionsFromFile(), transaction);

        // add the current transaction to pending transactions
        addPendingTransaction(transaction);

        // add the current transaction to database
        TransactionRepository.addTransaction(transaction);
    }

    private void flagThreeDistinctService(Transaction transaction) {
        List<Transaction> userTxList = filterTransaction(transaction, 5);

        // Check for frequent service usage within the window
        Set<String> distinctServices = new HashSet<>();

        for (Transaction t : userTxList) {
            distinctServices.add(t.getServiceID());
        }

        if (distinctServices.size() > MIN_DISTINCT_SERVICES) {
            System.out.println("Alert: user " + transaction.getUserID() +
                    " conducted transactions in " + distinctServices.size() +
                    " distinct services within a 5 minute window.");
        }
    }

    public void flagHighTransactionAmount(Transaction transaction) {
        List<Transaction> userTxList = filterTransaction(transaction, 1440);

        double totalAmount = 0;

        for (Transaction tx : userTxList) {
            totalAmount += tx.getAmount();
        }

        double averageAmount = totalAmount / userTxList.size();

        if (transaction.getAmount() > 5 * averageAmount) {
            System.out.println("Alert: Transaction amount for user "
                    + transaction.getUserID() + " is significantly higher than their average.");
        }

    }

    private List<Transaction> filterTransaction(Transaction transaction, int minutes) {
        List<Transaction> userTxList = TransactionRepository.getUserTransaction()
                .getOrDefault(transaction.getUserID(), new ArrayList<>());
        userTxList.add(transaction);

        // Remove transactions outside the window
        LocalDateTime currentTimestamp = transaction.getTimestamp();
        LocalDateTime minTimestamp = currentTimestamp.minusMinutes(minutes);
        userTxList.removeIf(t -> t.getTimestamp().isBefore(minTimestamp));

        return userTxList;
    }


    public void detectPingPong(List<Transaction> transactions, Transaction currentTransaction) {
        Map<String, LocalDateTime> serviceTransactionsMap = new HashMap<>();
        transactions.add(currentTransaction);

        String userID = currentTransaction.getUserID();

        for (Transaction transaction : transactions) {
            if (!transaction.getUserID().equals(userID)) {
                continue; // Skip transactions not related to the specified user
            }
            String serviceID = transaction.getServiceID();
            LocalDateTime timestamp = transaction.getTimestamp();

            if (serviceTransactionsMap.containsKey(serviceID)) {
                LocalDateTime prevTimestamp = serviceTransactionsMap.get(serviceID);
                long timeDifference = java.time.Duration.between(prevTimestamp, timestamp).toMinutes();
                if (timeDifference <= 10) {
                    String previousServiceID = getPreviousServiceID(serviceTransactionsMap, serviceID);
                    if (previousServiceID != null && !previousServiceID.equals(serviceID)) {
                        flagPingPongActivity(userID, previousServiceID, serviceID);
                    }
                }
            }
            serviceTransactionsMap.put(serviceID, timestamp);
        }
    }

    private List<Transaction> getAllTransactions(String userID) {
        List<Transaction> transactions = new ArrayList<>();
        for (List<Transaction> txList : pendingTransactions.values()) {
            transactions.addAll(txList);
        }
        transactions.addAll(TransactionRepository.getUserTransaction().getOrDefault(userID, new ArrayList<>()));
        return transactions;
    }

    private void addPendingTransaction(Transaction transaction) {
        LocalDateTime currentTimestamp = transaction.getTimestamp();
        String userID = transaction.getUserID();
        List<Transaction> userPendingTransactions = pendingTransactions.getOrDefault(userID, new ArrayList<>());
        // Add transaction to the correct position based on timestamp
        int index = 0;
        while (index < userPendingTransactions.size() && userPendingTransactions.get(index).getTimestamp().isBefore(currentTimestamp)) {
            index++;
        }
        userPendingTransactions.add(index, transaction);
        pendingTransactions.put(userID, userPendingTransactions);
    }

    // Method to process pending transactions in order
    public void processPendingTransactions() {
        for (List<Transaction> txList : pendingTransactions.values()) {
            for (Transaction transaction : txList) {
                processTransaction(transaction);
            }
        }
        pendingTransactions.clear();
    }

    private static String getPreviousServiceID(Map<String, LocalDateTime> serviceTransactionsMap, String currentServiceID) {
        for (String key : serviceTransactionsMap.keySet()) {
            if (!key.equals(currentServiceID)) {
                return key;
            }
        }
        return null;
    }

    private static void flagPingPongActivity(String userID, String previousServiceID, String currentServiceID) {
        System.out.println("Alert: Ping-pong activity detected for user " + userID +
                " between services " + previousServiceID + " and " + currentServiceID);
    }

}