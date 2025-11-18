import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ConcurrentSortedListTest {
    // Configuration
    static final int THREAD_COUNT = 7;
    static final int LIST_LENGTH = 50_000;
    static final int VALUE_RANGE = 80_000;
    static final int RANDOM_SEED = 0;

    // Global Counters (Must be reset before each test run)
    static final AtomicInteger successContains = new AtomicInteger(0);
    static final AtomicInteger failContains = new AtomicInteger(0);
    static final AtomicInteger successRemove = new AtomicInteger(0);
    static final AtomicInteger failRemove = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting Comprehensive Performance Test...");
        System.out.printf("Threads: %d | List Length: %d | Value Range: %d%n",
                THREAD_COUNT, LIST_LENGTH, VALUE_RANGE);

        // 1. Test SyncList (Coarse-Grained Synchronization)
        runTest("SyncList", SyncList::new);

        // 2. Test LockList (ReentrantLock)
        runTest("LockList", LockList::new);

        // 3. Test RWLockList (ReadWriteLock - High Performance)
        runTest("RWLockList", RWLockList::new);

        System.out.println("All tests completed.");
    }

    private static void runTest(String listName, Supplier<SortedList> listConstructor) throws InterruptedException {
        System.out.println("\n==========================================");
        System.out.println("Testing Implementation: " + listName);
        System.out.println("==========================================");

        // A. Reset state for this run
        SortedList list = listConstructor.get();
        resetCounters();

        // Generate Random Data (Same seed for fair comparison)
        Random masterRand = new Random(RANDOM_SEED);
        int[] inputData = new int[LIST_LENGTH];
        for (int i = 0; i < LIST_LENGTH; i++) {
            inputData[i] = masterRand.nextInt(VALUE_RANGE);
        }

        // --- Phase 1: ADD ---
        long startAdd = System.currentTimeMillis();
        runParallelTask(THREAD_COUNT, (threadId) -> {
            int chunk = LIST_LENGTH / THREAD_COUNT;
            int start = threadId * chunk;
            int end = (threadId == THREAD_COUNT - 1) ? LIST_LENGTH : start + chunk;
            for (int i = start; i < end; i++) {
                list.add(inputData[i]);
            }
        });
        long durationAdd = System.currentTimeMillis() - startAdd;
        System.out.printf("%-10s Execution Time: %d ms%n", "ADD", durationAdd);

        if (!list.isSorted()) System.err.println("ERROR: List is not sorted!");
        System.out.println("List length after adds: " + list.size());

        // --- Phase 2: CONTAINS ---
        long startContains = System.currentTimeMillis();
        runParallelTask(THREAD_COUNT, (threadId) -> {
            Random threadRand = new Random(threadId + 100);
            for (int i = 0; i < LIST_LENGTH / THREAD_COUNT; i++) {
                int target = threadRand.nextInt(VALUE_RANGE);
                if (list.contains(target)) {
                    successContains.incrementAndGet();
                } else {
                    failContains.incrementAndGet();
                }
            }
        });
        long durationContains = System.currentTimeMillis() - startContains;
        System.out.printf("%-10s Execution Time: %d ms%n", "CONTAINS", durationContains);
        System.out.printf("Stats: Successes found: %d, Failures found: %d%n",
                successContains.get(), failContains.get());

        // --- Phase 3: REMOVE ---
        long startRemove = System.currentTimeMillis();
        runParallelTask(THREAD_COUNT, (threadId) -> {
            Random threadRand = new Random(threadId + 200);
            for (int i = 0; i < (LIST_LENGTH / 2) / THREAD_COUNT; i++) {
                int target = threadRand.nextInt(VALUE_RANGE);
                if (list.remove(target)) {
                    successRemove.incrementAndGet();
                } else {
                    failRemove.incrementAndGet();
                }
            }
        });
        long durationRemove = System.currentTimeMillis() - startRemove;
        System.out.printf("%-10s Execution Time: %d ms%n", "REMOVE", durationRemove);

        if (!list.isSorted()) System.err.println("ERROR: List is not sorted after remove!");
        System.out.println("List length after remove: " + list.size());
        System.out.printf("Stats: Successes removed: %d, Failures removed: %d%n",
                successRemove.get(), failRemove.get());
    }

    private static void resetCounters() {
        successContains.set(0);
        failContains.set(0);
        successRemove.set(0);
        failRemove.set(0);
    }

    private static void runParallelTask(int numThreads, Task task) throws InterruptedException {
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> task.execute(threadId));
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    interface Task {
        void execute(int threadId);
    }
}