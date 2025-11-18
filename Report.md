# Performance Analysis: Concurrent Linked Lists
**Student:** Tarek Mourad  
**Subject:** Concurrent Programming  

## 1. Core Concepts
* **Lock Granularity:** Shifted from Coarse-Grained (locking entire list) to Fine-Grained (locking specific nodes) to reduce contention.
* **Reader-Writer Separation:** Used `ReentrantReadWriteLock` to allow simultaneous read access while enforcing exclusive write access.
* **Object-Based Isolation:** implemented "Hand-over-Hand" locking to isolate specific nodes, enabling parallel modification of the list structure.

## 2. Performance Data
*Test Configuration: 7 Threads | 50,000 Items | Value Range: 0-80,000*

| Implementation | Operation | Time (ms) | Result |
| :--- | :--- | :--- | :--- |
| **SyncList** | Add | 5025 | Baseline |
| *(Intrinsic Lock)* | **Contains** | **12062** | **Slowest (Serial Read)** |
| | Remove | 6147 | |
| **RWLockList** | Add | 5218 | No write benefit |
| *(ReadWriteLock)* | **Contains** | **1850** | **~6.5x Speedup (Parallel Read)** |
| | Remove | 6491 | Exclusive lock cost |

## 3. Key Findings

### A. Read Operations (The Speedup)
* **Result:** `RWLockList` `contains()` (1850ms) was **~6.5x faster** than `SyncList` (12062ms).
* **Analysis:** This approaches the theoretical maximum speedup for 7 threads ($12062 / 7 \approx 1723$). Proof that read operations scaled linearly because threads did not block each other.



### B. Write Operations (The Cost)
* **Result:** `RWLockList` `add()` (5218ms) was slightly slower than `SyncList` (5025ms).
* **Analysis:** Write locks behave as exclusive Mutexes. There is no parallelism gain for writes, only the added overhead of managing the complex lock object.

### C. Fine-Grained Isolation
* **Result:** `FineGrainedList` processed 20,000 concurrent removals in **225ms**.
* **Verification:** List remained sorted (`isSorted(): true`).
* **Conclusion:** "Hand-over-Hand" locking successfully isolated thread operations to specific nodes, allowing parallel modification without corrupting the list pointers.
