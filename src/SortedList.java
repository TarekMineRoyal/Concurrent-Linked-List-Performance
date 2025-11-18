import java.util.List;

public interface SortedList {
    void add(int value);
    boolean remove(int value);
    boolean contains(int value);
    int size();
    boolean isSorted();

    // New requirement for Part 2
    int removeAll(List<Integer> values);
}