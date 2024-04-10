# Real-Time Fraud Detection System

## Overview
This project is a prototype of a real-time fraud detection system developed in Java. It processes a stream of transactions and identifies potential fraudulent patterns based on certain criteria.

## Setup
1. Clone the repository to your local machine.
2. Ensure you have Java installed on your system.
3. Navigate to the project directory.

## How to Run
1. Compile the Java files:
   ```
   javac Transaction.java
   javac TransactionRepository.java
   javac FraudDetectionService.java
2. Run the main class `FraudDetectionSystem`:
   ```
   java FraudDetectionSystem
   ```
3. The program will process the sample transactions and print alerts for any suspicious activity detected.

## Algorithm Description
- **Multi-service Activity Detection**: Checks if a user conducts transactions in more than 3 distinct services within a 5-minute window.
- **High Transaction Amount Detection**: Identifies transactions that are significantly higher than the user's average transaction amount in the last 24 hours.
- **Ping-Pong Activity Detection**: Detects transactions bouncing back and forth between two services within a 10-minute window.

## Constraints
- The solution is implemented in Java without relying on external libraries for anomaly detection.
- Focus is on algorithm design for efficient processing and analysis of transaction streams.

## Handling Out-of-Order Events
The solution handles out-of-order events by temporarily storing pending transactions based on their timestamps.
Transactions are processed in the correct order to ensure accurate fraud detection, addressing network latency challenges in a distributed system.
## Test Dataset
The transactions.txt file contains mock database to validate the functionality of the fraud detection algorithms. Additional test cases can be added as needed.

## Assumptions
- The input data format remains consistent with the provided example.
- Real-time processing is simulated using a predefined list of transactions.

## Contributor
- Daniju Babajide
