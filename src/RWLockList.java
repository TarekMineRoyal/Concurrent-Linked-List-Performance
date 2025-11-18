import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;

public class RWLockList implements SortedList {
    private final Node head;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public RWLockList() {
        this.head = new Node(Integer.MIN_VALUE);
    }

    @Override
    public void add(int value) {
        writeLock.lock();
        try {
            Node pred = head;
            Node curr = head.next;
            while (curr != null && curr.value < value) {
                pred = curr;
                curr = curr.next;
            }
            Node newNode = new Node(value);
            newNode.next = curr;
            pred.next = newNode;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean remove(int value) {
        writeLock.lock();
        try {
            Node pred = head;
            Node curr = head.next;
            while (curr != null && curr.value < value) {
                pred = curr;
                curr = curr.next;
            }
            if (curr != null && curr.value == value) {
                pred.next = curr.next;
                return true;
            }
            return false;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean contains(int value) {
        readLock.lock();
        try {
            Node curr = head.next;
            while (curr != null && curr.value < value) {
                curr = curr.next;
            }
            return curr != null && curr.value == value;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            int count = 0;
            Node curr = head.next;
            while (curr != null) {
                count++;
                curr = curr.next;
            }
            return count;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isSorted() {
        readLock.lock();
        try {
            Node curr = head.next;
            while (curr != null && curr.next != null) {
                if (curr.value > curr.next.value) return false;
                curr = curr.next;
            }
            return true;
        } finally {
            readLock.unlock();
        }
    }

    // Inner Node Class
    private static class Node {
        int value;
        Node next;
        Node(int value) { this.value = value; this.next = null; }
    }

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
}