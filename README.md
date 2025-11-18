# Concurrent Linked List Performance Analysis

## Project Overview
This project implements and benchmarks three different synchronization strategies for a Sorted Linked List in Java. The goal is to analyze the trade-offs between coarse-grained locking, fine-grained locking, and Read-Write locks under high-concurrency workloads.

## Implementations
1. **SyncList:** Uses Java's `synchronized` keyword (Coarse-Grained).
2. **LockList:** Uses `ReentrantLock` (Coarse-Grained).
3. **RWLockList:** Uses `ReentrantReadWriteLock` to allow concurrent readers.
4. **FineGrainedList:** Uses "Hand-over-Hand" locking (Lock Coupling) for object-based isolation (Part 2).

## Benchmarks
The project includes a comprehensive performance test (`ConcurrentSortedListTest.java`) that simulates a high-load environment:
* **Threads:** 7
* **List Length:** 50,000 items
* **Value Range:** 0 - 80,000

### Key Results
* **Read Operations:** `RWLockList` achieved a **~6.5x speedup** compared to `SyncList` during the `contains()` phase.
* **Write Operations:** Fine-grained locking demonstrated successful isolation during parallel batch removals without corrupting the list structure.

## How to Run
1. Clone the repository.
2. Open the project in IntelliJ IDEA or Eclipse.
3. Run `src/ConcurrentSortedListTest.java` to see the performance comparison table.
4. Run `src/PartTwoTest.java` to verify the Fine-Grained Locking logic.
