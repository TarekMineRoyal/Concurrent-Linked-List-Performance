import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class PartTwoTest {
    static final int THREAD_COUNT = 4;
    static final int LIST_LENGTH = 20_000;
    static final int GROUP_SIZE = 500; // Size of group to remove per thread

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Part 2: Object-Based Isolation Test (FineGrainedList) ===");

        SortedList list = new FineGrainedList(); // Use the new Fine-Grained implementation

        // 1. Populate List
        Random rand = new Random(0);
        for (int i = 0; i < LIST_LENGTH; i++) {
            list.add(rand.nextInt(50_000));
        }
        System.out.println("Initial Size: " + list.size());

        // 2. Create Groups of numbers to remove
        List<List<Integer>> removeGroups = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            List<Integer> group = new ArrayList<>();
            for (int j = 0; j < GROUP_SIZE; j++) {
                group.add(rand.nextInt(50_000));
            }
            removeGroups.add(group);
        }

        // 3. Run Parallel Batch Removal
        AtomicInteger totalRemoved = new AtomicInteger(0);
        long start = System.currentTimeMillis();

        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                // Each thread calls removeAll with its own group
                int count = list.removeAll(removeGroups.get(threadId));
                totalRemoved.addAndGet(count);
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();
        long end = System.currentTimeMillis();

        // 4. Results
        System.out.println("Execution Time: " + (end - start) + " ms");
        System.out.println("Total items removed: " + totalRemoved.get());
        System.out.println("Final List Size: " + list.size());
        System.out.println("Is Sorted? " + list.isSorted());

        if (list.isSorted()) {
            System.out.println("SUCCESS: Fine-Grained Locking maintained list integrity.");
        } else {
            System.err.println("FAILURE: List is corrupted.");
        }
    }
}