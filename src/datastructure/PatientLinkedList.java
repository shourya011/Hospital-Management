package datastructure;

import model.Patient;

/**
 * PatientLinkedList class implements a custom singly linked list
 * Used to maintain in-memory patient records during session
 */
public class PatientLinkedList {
    
    /**
     * Inner class representing a node in the linked list
     */
    private class Node {
        Patient data;
        Node next;
        
        Node(Patient data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private int size;
    
    /**
     * Constructor initializing empty linked list
     */
    public PatientLinkedList() {
        this.head = null;
        this.size = 0;
    }
    
    /**
     * Add a patient to the beginning of the list
     * @param patient Patient to be added
     */
    public void addFirst(Patient patient) {
        Node newNode = new Node(patient);
        newNode.next = head;
        head = newNode;
        size++;
    }
    
    /**
     * Add a patient to the end of the list
     * @param patient Patient to be added
     */
    public void addLast(Patient patient) {
        Node newNode = new Node(patient);
        
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    /**
     * Add a patient at a specific position
     * @param index Position where patient should be added (0-indexed)
     * @param patient Patient to be added
     */
    public void add(int index, Patient patient) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        
        if (index == 0) {
            addFirst(patient);
        } else {
            Node newNode = new Node(patient);
            Node current = head;
            
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            
            newNode.next = current.next;
            current.next = newNode;
            size++;
        }
    }
    
    /**
     * Add a patient (default adds to end)
     * @param patient Patient to be added
     */
    public void add(Patient patient) {
        addLast(patient);
    }
    
    /**
     * Remove a patient by patient ID
     * @param patientId Patient ID to be removed
     * @return true if patient was removed, false otherwise
     */
    public boolean remove(int patientId) {
        if (head == null) {
            return false;
        }
        
        // Remove from head
        if (head.data.getPatientId() == patientId) {
            head = head.next;
            size--;
            return true;
        }
        
        // Remove from rest of list
        Node current = head;
        while (current.next != null) {
            if (current.next.data.getPatientId() == patientId) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        
        return false;
    }
    
    /**
     * Remove patient at specific index
     * @param index Index of patient to be removed (0-indexed)
     * @return Patient object that was removed
     */
    public Patient removeAt(int index) {
        if (index < 0 || index >= size || head == null) {
            return null;
        }
        
        if (index == 0) {
            Patient data = head.data;
            head = head.next;
            size--;
            return data;
        }
        
        Node current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        
        Patient data = current.next.data;
        current.next = current.next.next;
        size--;
        return data;
    }
    
    /**
     * Search for a patient by patient ID
     * @param patientId Patient ID to search
     * @return Patient object if found, null otherwise
     */
    public Patient search(int patientId) {
        Node current = head;
        while (current != null) {
            if (current.data.getPatientId() == patientId) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
    
    /**
     * Search for a patient by phone number
     * @param phone Patient phone number
     * @return Patient object if found, null otherwise
     */
    public Patient searchByPhone(String phone) {
        Node current = head;
        while (current != null) {
            if (current.data.getPhone().equals(phone)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }
    
    /**
     * Get patient at specific index
     * @param index Index of patient (0-indexed)
     * @return Patient object if index is valid, null otherwise
     */
    public Patient get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        
        return current.data;
    }
    
    /**
     * Get the size of the linked list
     * @return number of patients in list
     */
    public int size() {
        return size;
    }
    
    /**
     * Check if the list is empty
     * @return true if list is empty, false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Clear all patients from the list
     */
    public void clear() {
        head = null;
        size = 0;
    }
    
    /**
     * Check if a specific patient exists in the list
     * @param patientId Patient ID to check
     * @return true if patient exists, false otherwise
     */
    public boolean contains(int patientId) {
        return search(patientId) != null;
    }
    
    /**
     * Display all patients in the linked list
     */
    public void displayAll() {
        if (head == null) {
            System.out.println("⚠ Linked List is empty!");
            return;
        }
        
        System.out.println("\n=== PATIENT LINKED LIST ===");
        System.out.println("Total Patients: " + size);
        System.out.println("----------------------------------------");
        
        Node current = head;
        int position = 1;
        while (current != null) {
            Patient p = current.data;
            System.out.printf("%d. %s (ID: %d, Phone: %s, Blood: %s)%n",
                    position++,
                    p.getFullName(),
                    p.getPatientId(),
                    p.getPhone(),
                    p.getBloodGroup());
            current = current.next;
        }
        System.out.println("----------------------------------------\n");
    }
    
    /**
     * Convert linked list to array
     * @return array of patients
     */
    public Patient[] toArray() {
        Patient[] arr = new Patient[size];
        Node current = head;
        int index = 0;
        
        while (current != null) {
            arr[index++] = current.data;
            current = current.next;
        }
        
        return arr;
    }
    
    /**
     * Reverse the linked list (in-place)
     */
    public void reverse() {
        if (size <= 1) {
            return;
        }
        
        Node prev = null;
        Node current = head;
        
        while (current != null) {
            Node nextTemp = current.next;
            current.next = prev;
            prev = current;
            current = nextTemp;
        }
        
        head = prev;
    }
}
