# Java Blockchain Implementation

A clean, object-oriented implementation of a blockchain core structure in Java. This project demonstrates fundamental cryptographic concepts like hashing, proof-of-work mining, and transaction management, forming the basis of a distributed ledger system.

## Overview

This repository contains the core `Block` class, a fundamental building block for a cryptocurrency or distributed ledger. Each block cryptographically links to the previous one, contains a list of transactions, and is secured through a proof-of-work mining process.

## Features

*   **Cryptographic Hashing:** Utilizes SHA-256 encryption to generate unique block identifiers, ensuring data integrity.
*   **Proof-of-Work (Mining):** Implements a mining process with an adjustable difficulty target to secure the network against tampering.
*   **Merkle Tree Root:** Efficiently calculates a single hash from all transactions in the block for verification purposes.
*   **Transaction Management:** Provides methods to add and validate transactions before including them in a block.
*   **Immutability:** The chain of cryptographic hashes ensures that past data cannot be altered without invalidating the entire subsequent chain.

## Code Structure

### The `Block` Class

#### Core Attributes:
*   `public String hash`: The digital fingerprint (hash) of the current block.
*   `public String previousHash`: The hash of the previous block in the chain, creating the linkage.
*   `public String merkleRoot`: The root hash of a Merkle Tree built from all transactions in the block.
*   `public ArrayList<Transaction> transactions`: The list of transactions contained within this block.
*   `private long timeStamp`: The time of block creation (in milliseconds since epoch).
*   `private int nonce`: A number varied during mining to find a valid hash.

#### Key Methods:
*   **`Block(String previousHash)`**
    The constructor. Initializes the block with a reference to the previous block's hash and sets the timestamp. It also calculates the initial hash.

*   **`public String getHash()`**
    Calculates and returns the SHA-256 hash of the block's critical components: the previous hash, timestamp, current nonce, and Merkle root. This function is called repeatedly during the mining process.

*   **`public void mine(int difficulty)`**
    The proof-of-work algorithm.
    1.  First, it calculates the Merkle root of all transactions.
    2.  It then defines a `target` string (e.g., "000" for difficulty 3).
    3.  It continuously increments the `nonce` and recalculates the block's `hash` until the hash starts with the number of leading zeros specified by the `difficulty` parameter.
    4.  Prints a message upon successful mining.

*   **`public boolean addTransaction(Transaction transaction)`**
    Adds a transaction to the block after performing validation.
    - For the genesis block (previousHash = "0"), transactions are typically added without complex checks.
    - For subsequent blocks, it calls `transaction.processTransaction()` to validate the transaction's authenticity and integrity (e.g., checking digital signatures).
    - Returns `true` if the transaction was added successfully, `false` otherwise.

### Dependencies

This `Block` class relies on two other critical components:

1.  **`Utility` Class:** A helper class containing static cryptographic functions.
    *   `String applyEncryption(String input)`: Performs the SHA-256 hashing algorithm.
    *   `String getMerkleRoot(ArrayList<Transaction> transactions)`: Calculates and returns the Merkle root hash for the given list of transactions.
    *   `String getDificultyString(int difficulty)`: Returns a String of leading zeros (e.g., `getDificultyString(3)` returns `"000"`).

2.  **`Transaction` Class:** A class representing a financial transaction within the blockchain. It is assumed to have a method:
    *   `boolean processTransaction()`: Verifies the transaction's validity (e.g., checking digital signatures and ensuring inputs are unspent).
