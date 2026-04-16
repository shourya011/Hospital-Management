package datastructure;

import model.Patient;
import java.util.LinkedList;

/**
 * PatientQueue class represents a queue of patients using LinkedList
 * Used for managing walk-in patient waiting order
 * Implements FIFO (First-In-First-Out) principle
 */
public class PatientQueue {
    private LinkedList<Patient> queue;
    
    /**
     * Constructor initializing the queue
     */
    public PatientQueue() {
        this.queue = new LinkedList<>();
    }
    
    /**
     * Add a patient to the end of the queue (enqueue)
     * @param patient Patient to be added to queue
     */
    public void enqueue(Patient patient) {
        queue.addLast(patient);
        System.out.println("✓ Patient " + patient.getFullName() + " added to queue");
    }
    
    /**
     * Remove and return the first patient from the queue (dequeue)
     * @return Patient object if queue is not empty, null otherwise
     */
    public Patient dequeue() {
        if (queue.isEmpty()) {
            System.out.println("⚠ Queue is empty!");
            return null;
        }
        Patient patient = queue.removeFirst();
        System.out.println("✓ Patient " + patient.getFullName() + " removed from queue");
        return patient;
    }
    
    /**
     * Get the first patient in the queue without removing
     * @return Patient object if queue is not empty, null otherwise
     */
    public Patient peek() {
        if (queue.isEmpty()) {
            return null;
        }
        return queue.getFirst();
    }
    
    /**
     * Check if the queue is empty
     * @return true if queue is empty, false otherwise
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }
    
    /**
     * Get the size of the queue
     * @return number of patients in the queue
     */
    public int size() {
        return queue.size();
    }
    
    /**
     * Display all patients in the queue
     */
    public void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("⚠ Queue is empty!");
            return;
        }
        
        System.out.println("\n=== PATIENT QUEUE ===");
        System.out.println("Total Patients in Queue: " + queue.size());
        System.out.println("----------------------------------------");
        
        int position = 1;
        for (Patient patient : queue) {
            System.out.printf("%d. %s (ID: %d, Phone: %s)%n",
                    position++,
                    patient.getFullName(),
                    patient.getPatientId(),
                    patient.getPhone());
        }
        System.out.println("----------------------------------------\n");
    }
    
    /**
     * Clear all patients from the queue
     */
    public void clear() {
        queue.clear();
        System.out.println("✓ Queue cleared");
    }
    
    /**
     * Check if a specific patient is in the queue
     * @param patientId Patient ID to search
     * @return true if patient is in queue, false otherwise
     */
    public boolean contains(int patientId) {
        for (Patient patient : queue) {
            if (patient.getPatientId() == patientId) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Remove a specific patient from the queue
     * @param patientId Patient ID to remove
     * @return true if patient was removed, false otherwise
     */
    public boolean removePatient(int patientId) {
        for (Patient patient : queue) {
            if (patient.getPatientId() == patientId) {
                queue.remove(patient);
                System.out.println("✓ Patient " + patient.getFullName() + " removed from queue");
                return true;
            }
        }
        System.out.println("⚠ Patient not found in queue");
        return false;
    }
    
    /**
     * Get patient at specific position in queue
     * @param position Position in queue (1-indexed)
     * @return Patient object if position is valid, null otherwise
     */
    public Patient getPatientAtPosition(int position) {
        if (position < 1 || position > queue.size()) {
            return null;
        }
        return queue.get(position - 1);
    }
    
    /**
     * Convert queue to array for display purposes
     * @return array of patients
     */
    public Patient[] toArray() {
        return queue.toArray(new Patient[0]);
    }
}
