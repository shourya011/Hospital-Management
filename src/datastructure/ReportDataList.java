package datastructure;

import java.util.ArrayList;

// Custom singly linked list implementation as required
public class ReportDataList<T> {
    
    // Inner Node class for data storage and linking
    private class Node {
        T data;
        Node next;
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    public ReportDataList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(T item) {
        Node newNode = new Node(item);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    public ArrayList<T> getAll() {
        ArrayList<T> list = new ArrayList<>();
        Node current = head;
        // Traverse custom list to convert to ArrayList for UI rendering
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }

    public int size() {
        return size;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}