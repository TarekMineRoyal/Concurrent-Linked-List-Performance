import java.util.List;
public class SyncList implements SortedList {
    private final Node head;

    public SyncList() {
        this.head = new Node(Integer.MIN_VALUE);
    }

    @Override
    public synchronized void add(int value) {
        Node pred = head;
        Node curr = head.next;
        while (curr != null && curr.value < value) {
            pred = curr;
            curr = curr.next;
        }
        Node newNode = new Node(value);
        newNode.next = curr;
        pred.next = newNode;
    }

    @Override
    public synchronized boolean remove(int value) {
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
    }

    @Override
    public synchronized boolean contains(int value) {
        Node curr = head.next;
        while (curr != null && curr.value < value) {
            curr = curr.next;
        }
        return curr != null && curr.value == value;
    }

    @Override
    public synchronized int size() {
        int count = 0;
        Node curr = head.next;
        while (curr != null) {
            count++;
            curr = curr.next;
        }
        return count;
    }

    @Override
    public synchronized boolean isSorted() {
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