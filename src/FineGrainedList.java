import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class FineGrainedList implements SortedList {
    private final Node head;

    public FineGrainedList() {
        this.head = new Node(Integer.MIN_VALUE);
    }

    @Override
    public void add(int value) {
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            if (curr != null) curr.lock.lock();
            try {
                while (curr != null && curr.value < value) {
                    pred.lock.unlock(); // Unlock previous
                    pred = curr;
                    curr = curr.next;
                    if (curr != null) curr.lock.lock(); // Lock next
                }
                // Found position
                Node newNode = new Node(value);
                newNode.next = curr;
                pred.next = newNode;
            } finally {
                if (curr != null) curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    @Override
    public boolean remove(int value) {
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            if (curr != null) curr.lock.lock();
            try {
                while (curr != null && curr.value < value) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    if (curr != null) curr.lock.lock();
                }

                if (curr != null && curr.value == value) {
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                if (curr != null) curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    @Override
    public boolean contains(int value) {
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            if (curr != null) curr.lock.lock();
            try {
                while (curr != null && curr.value < value) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    if (curr != null) curr.lock.lock();
                }
                return curr != null && curr.value == value;
            } finally {
                if (curr != null) curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    /**
     * PART 2 Implementation:
     * Removes a group of integers. Because we use Fine-Grained locking,
     * other threads can access the list while this method is processing
     * the group, as long as they are looking at different nodes.
     */
    @Override
    public int removeAll(List<Integer> values) {
        int count = 0;
        for (Integer val : values) {
            if (remove(val)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int size() {
        // Note: Getting exact size in fine-grained list is tricky without freezing everything.
        // This implementation iterates but size might change during counting.
        int count = 0;
        Node curr = head.next;
        while (curr != null) {
            count++;
            curr = curr.next;
        }
        return count;
    }

    @Override
    public boolean isSorted() {
        Node curr = head.next;
        while (curr != null && curr.next != null) {
            if (curr.value > curr.next.value) return false;
            curr = curr.next;
        }
        return true;
    }

    private static class Node {
        int value;
        Node next;
        // ISOLATION PER OBJECT: Each node has its own lock
        final Lock lock = new ReentrantLock();

        Node(int value) { this.value = value; this.next = null; }
    }
}