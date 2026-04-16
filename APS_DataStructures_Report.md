# Hospital Management System - APS (Data Structures) Report

## 1. Introduction

This report documents **Advanced Problem Solving (APS)** through **Data Structures** implementation in the **RBH (Rukmani Birla Hospital) Management System**. It covers custom implementations of data structures including Queues, Linked Lists, and their applications in hospital management.

---

## 2. Data Structures Used in the System

### 2.1 Overview

```
┌─────────────────────────────────────────────────┐
│     Hospital Management System                  │
│                                                 │
│  ┌──────────────────────────────────────────┐ │
│  │  Custom Data Structures                  │ │
│  ├──────────────────────────────────────────┤ │
│  │  1. PatientQueue (FIFO Queue)            │ │
│  │  2. PatientLinkedList (Doubly Linked)    │ │
│  │  3. ArrayList (Appointments)              │ │
│  │  4. HashMap (Doctor Lookup)               │ │
│  └──────────────────────────────────────────┘ │
│                                                │
│  ┌──────────────────────────────────────────┐ │
│  │  Use Cases                               │ │
│  ├──────────────────────────────────────────┤ │
│  │  • PatientQueue: Appointment tokens      │ │
│  │  • LinkedList: Patient management        │ │
│  │  • ArrayList: Dynamic collections        │ │
│  │  • HashMap: Fast lookups                 │ │
│  └──────────────────────────────────────────┘ │
│                                                 │
└─────────────────────────────────────────────────┘
```

---

## 3. Queue Data Structure

### 3.1 PatientQueue Implementation

**Queue Concept (FIFO - First In First Out):**
```
INSERT → [1] → [2] → [3] → [4] → [5] → REMOVE
               HEAD                    TAIL
         Head (First patient)    Tail (Last patient)
```

**PatientQueue Class:**
```java
public class PatientQueue {
    private int maxSize;
    private Patient[] queueArray;
    private int front;
    private int rear;
    private int nItems;
    
    /**
     * Constructor - initializes queue with maximum size
     */
    public PatientQueue(int size) {
        this.maxSize = size;
        this.queueArray = new Patient[maxSize];
        this.front = 0;
        this.rear = -1;
        this.nItems = 0;
    }
    
    /**
     * Insert patient at rear of queue
     */
    public void enqueue(Patient patient) {
        if (isFull()) {
            System.out.println("Queue is full!");
            return;
        }
        
        // Wrap around if necessary
        rear = (rear + 1) % maxSize;
        queueArray[rear] = patient;
        nItems++;
        
        System.out.println("✓ Patient " + patient.getFirstName() + 
                          " added to queue. Token: " + nItems);
    }
    
    /**
     * Remove patient from front of queue
     */
    public Patient dequeue() {
        if (isEmpty()) {
            System.out.println("Queue is empty!");
            return null;
        }
        
        Patient patient = queueArray[front];
        queueArray[front] = null;  // Clear reference
        
        // Wrap around if necessary
        front = (front + 1) % maxSize;
        nItems--;
        
        System.out.println("✓ Patient " + patient.getFirstName() + 
                          " removed from queue");
        return patient;
    }
    
    /**
     * Peek at front patient without removing
     */
    public Patient peek() {
        if (isEmpty()) {
            return null;
        }
        return queueArray[front];
    }
    
    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return (nItems == 0);
    }
    
    /**
     * Check if queue is full
     */
    public boolean isFull() {
        return (nItems == maxSize);
    }
    
    /**
     * Get number of items in queue
     */
    public int size() {
        return nItems;
    }
    
    /**
     * Display all patients in queue
     */
    public void display() {
        System.out.println("\n═══ PATIENT QUEUE ═══");
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }
        
        for (int i = 0; i < nItems; i++) {
            int index = (front + i) % maxSize;
            Patient p = queueArray[index];
            System.out.println((i+1) + ". " + p.getFirstName() + " " + 
                             p.getLastName() + " - Phone: " + p.getPhone());
        }
        System.out.println("════════════════════");
    }
}
```

### 3.2 Queue Operations Time Complexity

| Operation | Time | Space |
|-----------|------|-------|
| enqueue() | O(1) | O(n) |
| dequeue() | O(1) | O(n) |
| peek() | O(1) | O(n) |
| isEmpty() | O(1) | O(n) |
| isFull() | O(1) | O(n) |

---

### 3.3 Hospital Queue Use Case

**Appointment Token System:**
```
┌──────────────────────────────────────────┐
│     Appointment Queue                    │
├──────────────────────────────────────────┤
│                                          │
│  FRONT [Raj Kumar] Token: 101           │
│         ↓                                │
│        [Yuki Tanaka] Token: 102         │
│         ↓                                │
│        [Arjun Singh] Token: 103         │
│         ↓                                │
│        [Neha Gupta] Token: 104          │
│         ↓                                │
│  REAR  [Vikas Verma] Token: 105         │
│                                          │
└──────────────────────────────────────────┘

Workflow:
1. Patient arrives → enqueue()
2. Token number assigned (queue position)
3. Patient waits in queue
4. Doctor available → dequeue()
5. Serve patient
6. Next patient moved to front
```

**Code Example:**
```java
public class AppointmentQueueManager {
    private PatientQueue appointmentQueue;
    
    public AppointmentQueueManager(int maxSize) {
        this.appointmentQueue = new PatientQueue(maxSize);
    }
    
    /**
     * Add patient to appointment queue
     */
    public void addPatientToQueue(Patient patient) {
        appointmentQueue.enqueue(patient);
    }
    
    /**
     * Call next patient from queue
     */
    public Patient callNextPatient() {
        Patient patient = appointmentQueue.dequeue();
        if (patient != null) {
            System.out.println("Calling: " + patient.getFirstName());
        }
        return patient;
    }
    
    /**
     * Check who's next without removing
     */
    public Patient peekNextPatient() {
        return appointmentQueue.peek();
    }
    
    /**
     * Current queue status
     */
    public void displayQueueStatus() {
        appointmentQueue.display();
    }
}
```

---

## 4. Linked List Data Structure

### 4.1 PatientLinkedList Implementation

**Linked List Concept (Doubly Linked):**
```
    ┌──────────────────────────────────────────────┐
    │  Doubly Linked List Structure:              │
    └──────────────────────────────────────────────┘

NULL ← [Prev|Data|Next] ↔ [Prev|Data|Next] ↔ [Prev|Data|Next] → NULL
       (Node 1)           (Node 2)           (Node 3)
       HEAD                                   TAIL

Benefits:
- Bidirectional traversal
- Efficient insertion/deletion
- Dynamic size
```

**PatientLinkedList Class:**
```java
public class PatientLinkedList {
    
    /**
     * Inner Node class
     */
    private class Node {
        Patient data;
        Node prev;
        Node next;
        
        Node(Patient data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }
    
    private Node head;
    private Node tail;
    private int size;
    
    /**
     * Constructor - initialize empty list
     */
    public PatientLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    /**
     * Insert patient at beginning
     */
    public void insertAtBeginning(Patient patient) {
        Node newNode = new Node(patient);
        
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }
    
    /**
     * Insert patient at end
     */
    public void insertAtEnd(Patient patient) {
        Node newNode = new Node(patient);
        
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }
    
    /**
     * Insert patient at specific position
     */
    public void insertAtPosition(int position, Patient patient) {
        if (position < 1 || position > size + 1) {
            System.out.println("Invalid position!");
            return;
        }
        
        if (position == 1) {
            insertAtBeginning(patient);
            return;
        }
        
        if (position == size + 1) {
            insertAtEnd(patient);
            return;
        }
        
        Node newNode = new Node(patient);
        Node current = getNodeAtPosition(position);
        
        newNode.next = current;
        newNode.prev = current.prev;
        current.prev.next = newNode;
        current.prev = newNode;
        size++;
    }
    
    /**
     * Delete patient by patient ID
     */
    public boolean delete(int patientId) {
        if (isEmpty()) {
            return false;
        }
        
        Node current = head;
        while (current != null) {
            if (current.data.getPatientId() == patientId) {
                // Found node to delete
                if (current == head && current == tail) {
                    // Only one node
                    head = tail = null;
                } else if (current == head) {
                    // Delete head
                    head = current.next;
                    head.prev = null;
                } else if (current == tail) {
                    // Delete tail
                    tail = current.prev;
                    tail.next = null;
                } else {
                    // Delete middle node
                    current.prev.next = current.next;
                    current.next.prev = current.prev;
                }
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    /**
     * Search patient by phone
     */
    public Patient search(String phone) {
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
     * Get node at specific position
     */
    private Node getNodeAtPosition(int position) {
        if (position <= size / 2) {
            // Search from head
            Node current = head;
            for (int i = 1; i < position; i++) {
                current = current.next;
            }
            return current;
        } else {
            // Search from tail (optimization)
            Node current = tail;
            for (int i = size; i > position; i--) {
                current = current.prev;
            }
            return current;
        }
    }
    
    /**
     * Display list forward
     */
    public void displayForward() {
        System.out.println("\n═══ PATIENTS (Forward) ═══");
        Node current = head;
        int count = 1;
        
        while (current != null) {
            System.out.println(count + ". " + current.data.getFirstName() + 
                             " " + current.data.getLastName() + 
                             " (ID: " + current.data.getPatientId() + ")");
            current = current.next;
            count++;
        }
        System.out.println("Size: " + size);
        System.out.println("═════════════════════════");
    }
    
    /**
     * Display list backward
     */
    public void displayBackward() {
        System.out.println("\n═══ PATIENTS (Backward) ═══");
        Node current = tail;
        int count = 1;
        
        while (current != null) {
            System.out.println(count + ". " + current.data.getFirstName() + 
                             " " + current.data.getLastName() + 
                             " (ID: " + current.data.getPatientId() + ")");
            current = current.prev;
            count++;
        }
        System.out.println("═══════════════════════════");
    }
    
    /**
     * Check if list is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Get size of list
     */
    public int getSize() {
        return size;
    }
}
```

### 4.2 LinkedList Operations Time Complexity

| Operation | Time | Notes |
|-----------|------|-------|
| insertAtBeginning() | O(1) | No traversal needed |
| insertAtEnd() | O(1) | Direct tail access |
| insertAtPosition(k) | O(n) | Binary search optimization used |
| delete(id) | O(n) | Worst case: traverse entire list |
| search(phone) | O(n) | Linear search |
| displayForward() | O(n) | Must traverse all nodes |
| displayBackward() | O(n) | Must traverse all nodes |

### 4.3 Comparison: Array vs Linked List

| Operation | Array | Linked List |
|-----------|-------|-------------|
| **Access** | O(1) | O(n) |
| **Search** | O(n) | O(n) |
| **Insert (beginning)** | O(n) | O(1) |
| **Insert (end)** | O(1) | O(1) |
| **Insert (middle)** | O(n) | O(n) |
| **Delete (beginning)** | O(n) | O(1) |
| **Delete (end)** | O(1) | O(1) |
| **Delete (middle)** | O(n) | O(n) |
| **Space Complexity** | O(n) | O(n) |
| **Memory Usage** | Contiguous | Scattered |

---

### 4.4 Hospital LinkedList Use Case

**Patient Management System:**
```
Doubly Linked List of Patients:

NULL ← [ID:1|Raj Kumar] ↔ [ID:2|Yuki Tanaka] ↔ [ID:3|Arjun Singh] → NULL

Operations:
1. Add new patient → insertAtEnd()
2. Remove discharged patient → delete()
3. Find patient by phone → search()
4. Display all patients → displayForward()
5. Display in reverse order → displayBackward()
6. Insert at specific position → insertAtPosition()
```

**Use Case Example:**
```java
public class PatientRegistry {
    private PatientLinkedList patientList;
    
    public PatientRegistry() {
        this.patientList = new PatientLinkedList();
    }
    
    /**
     * Register new patient
     */
    public void registerPatient(Patient patient) {
        patientList.insertAtEnd(patient);
        System.out.println("✓ Patient registered: " + patient.getFirstName());
    }
    
    /**
     * Remove patient from registry
     */
    public void removePatient(int patientId) {
        if (patientList.delete(patientId)) {
            System.out.println("✓ Patient removed");
        } else {
            System.out.println("✗ Patient not found");
        }
    }
    
    /**
     * Find patient by phone
     */
    public Patient findPatientByPhone(String phone) {
        return patientList.search(phone);
    }
    
    /**
     * Display all registered patients
     */
    public void displayAllPatients() {
        patientList.displayForward();
    }
}
```

---

## 5. Other Data Structures Used

### 5.1 ArrayList (Dynamic Array)

**Used for:** Patient lists, appointment lists, doctor lists

```java
// ArrayList for appointments
ArrayList<Appointment> appointments = new ArrayList<>();

// Add appointment
appointments.add(new Appointment(...));

// Characteristics:
// - O(1) access by index
// - O(n) insertion/deletion at arbitrary position
// - Dynamic resizing (grows as needed)
// - Generic type safety
```

**Growth Strategy:**
```
Initial capacity: 10 elements
When full: 10 × 1.5 = 15 elements (50% increase)
New capacity: 15 elements
```

### 5.2 HashMap (Hash Table)

**Used for:** Fast doctor lookup by specialization

```java
// Doctor lookup by specialization
HashMap<String, ArrayList<Doctor>> doctorsBySpecialty = new HashMap<>();

// Add doctors
ArrayList<Doctor> cardiologists = new ArrayList<>();
cardiologists.add(new Doctor("Dr. Rajesh Kumar", "Cardiologist"));
doctorsBySpecialty.put("Cardiologist", cardiologists);

// Search: O(1) average case
ArrayList<Doctor> result = doctorsBySpecialty.get("Cardiologist");
```

**Time Complexity:**
| Operation | Average | Worst Case |
|-----------|---------|-----------|
| get() | O(1) | O(n) |
| put() | O(1) | O(n) |
| remove() | O(1) | O(n) |
| containsKey() | O(1) | O(n) |

---

## 6. Algorithm Analysis

### 6.1 Big-O Complexity Chart

```
       Operations
       ↑
  100 │
       │  O(n²) ████████████████ (Quadratic)
       │        ████████████
   50 │        ████████
       │        ████
   10 │  O(n)  ░░░░░░░░░░░░░░░░ (Linear)
       │        ░░░░░░░░░░░
    5 │  O(log n) ▓▓▓▓▓▓ (Logarithmic)
       │          ▓▓▓▓
    1 │  O(1)  ─────────────── (Constant)
       └────────────────────────────→ Data Size (n)
       1   10  100  1000  10000
```

**Complexity Classification:**
- **O(1)** - Constant: Best case, instant operations
- **O(log n)** - Logarithmic: Binary search
- **O(n)** - Linear: Single loop through data
- **O(n log n)** - Linear-logarithmic: Efficient sorting
- **O(n²)** - Quadratic: Nested loops, bubble sort
- **O(2ⁿ)** - Exponential: Avoid in production

### 6.2 Hospital System Complexities

| Operation | Complexity | Reason |
|-----------|-----------|--------|
| Add patient to queue | O(1) | Direct insertion |
| Remove from queue | O(1) | Direct deletion from front |
| Get next patient | O(1) | Peek at front |
| Insert in LinkedList (end) | O(1) | Direct tail access |
| Search by phone | O(n) | Linear search through list |
| Get all patients | O(n) | Retrieve from database |
| Find doctor by specialty | O(1) avg | HashMap lookup |
| Book appointment | O(1) | Database insert |

---

## 7. Sorting & Searching Algorithms

### 7.1 Sorting in Hospital System

**Current Implementation: Database Sorting**
```sql
-- Sort patients by ID
SELECT * FROM patients ORDER BY patient_id ASC;

-- Sort appointments by token
SELECT * FROM appointments ORDER BY token_number ASC;
```

**If implemented in Java (Algorithm Comparison):**

| Algorithm | Time | Space | Stable | Use Case |
|-----------|------|-------|--------|----------|
| **Bubble Sort** | O(n²) | O(1) | Yes | Educational only |
| **Insertion Sort** | O(n²) | O(1) | Yes | Small data sets |
| **Merge Sort** | O(n log n) | O(n) | Yes | Large data sets |
| **Quick Sort** | O(n log n) avg | O(log n) | No | General purpose |
| **Heap Sort** | O(n log n) | O(1) | No | Guaranteed O(n log n) |

**Example: Sorting Patients by ID**
```java
// QuickSort approach (O(n log n))
public void sortPatientsByID(ArrayList<Patient> patients) {
    quickSort(patients, 0, patients.size() - 1);
}

private void quickSort(ArrayList<Patient> arr, int low, int high) {
    if (low < high) {
        int pi = partition(arr, low, high);
        quickSort(arr, low, pi - 1);
        quickSort(arr, pi + 1, high);
    }
}
```

### 7.2 Searching Algorithms

**Linear Search:**
```java
// O(n) - Scan list until found
public Patient searchByPhone(ArrayList<Patient> patients, String phone) {
    for (Patient p : patients) {
        if (p.getPhone().equals(phone)) {
            return p;
        }
    }
    return null;  // Not found
}
```

**Binary Search (requires sorted data):**
```java
// O(log n) - Only works on sorted arrays
public int binarySearch(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    
    while (left <= right) {
        int mid = left + (right - left) / 2;
        
        if (arr[mid] == target) {
            return mid;  // Found
        } else if (arr[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    return -1;  // Not found
}
```

---

## 8. Memory Management

### 8.1 Memory Layout

**Stack Memory (Fast, Limited):**
```java
PatientQueue queue = new PatientQueue(100);  // Reference: stack
// Patient[] array: heap (referenced from stack)
```

**Heap Memory (Slow, Large):**
```
Heap
┌─────────────────────────────────┐
│  Patient objects                │
│  Queue array                    │
│  Linked List nodes              │
│  ArrayLists                     │
│                                 │
└─────────────────────────────────┘
```

### 8.2 Garbage Collection

**Automatic Memory Cleanup:**
```java
// Create object
PatientQueue queue = new PatientQueue(100);

// Use object
queue.enqueue(patient);

// Remove reference
queue = null;

// Garbage collector will clean up (automatic)
// No manual memory management needed (unlike C++)
```

### 8.3 Memory Efficiency

| Data Structure | Memory Usage | When to Use |
|---|---|---|
| Array | O(n) - Fixed | Known size, direct access |
| LinkedList | O(n) - Extra pointers | Frequent insertions/deletions |
| Queue | O(n) | FIFO operations |
| HashMap | O(n) - Load factor | Fast lookups |
| Stack | O(n) | LIFO operations |

---

## 9. Practical Applications

### 9.1 Queue Applications in Hospital

```
1. APPOINTMENT QUEUE
   - Patients arrive in order
   - Tokens assigned sequentially
   - Serve in FIFO order
   
2. PRESCRIPTION QUEUE
   - Pharmacist processes in order
   - First come, first served
   
3. BILLING QUEUE
   - Patients billed in order
   - Fair system, no jumping
```

### 9.2 LinkedList Applications in Hospital

```
1. PATIENT HISTORY
   - Maintain insertion order
   - Bidirectional navigation
   - Easy removal when discharged
   
2. VISIT RECORDS
   - Store chronological visits
   - Forward/backward traversal
   - Insert between events
```

### 9.3 Hash Table Applications

```
1. DOCTOR LOOKUP
   - By specialization: O(1)
   - By availability: O(1)
   - By location: O(1)
   
2. PATIENT RECORDS
   - By phone number: O(1)
   - By ID: O(1)
   - By email: O(1)
```

---

## 10. Advanced Concepts

### 10.1 Circular Queue

**Concept:** Rear wraps around to front when queue is full

```
Linear Queue:
[1][2][3][ ][ ] - Wasteful space after removing elements

Circular Queue:
Remove [1] → [ ][2][3][ ][ ]
           - Front pointer moves
           - [1]'s space reused when rear wraps

Structure at runtime:
        [5]
    [4]     [1]
   [3]       [2]
```

**Implementation (as used in PatientQueue):**
```java
public void enqueue(Patient patient) {
    rear = (rear + 1) % maxSize;  // Wrap around
    queueArray[rear] = patient;
    nItems++;
}

public Patient dequeue() {
    Patient patient = queueArray[front];
    front = (front + 1) % maxSize;  // Wrap around
    nItems--;
    return patient;
}
```

### 10.2 Stack (Inverse of Queue)

**LIFO - Last In First Out:**
```
Use case: Browser back button, undo functionality

[Button 1]
[Button 2]
[Button 3] ← Top (Last pressed)

Press undo:
Remove [Button 3]
Remove [Button 2]
Remove [Button 1]
```

### 10.3 Tree Structures (Future Enhancement)

**Could use for specialization hierarchy:**
```
        Root (All Doctors)
        /      |      \
    Cardio   Neuro   Ortho
    /   \     / \     /  \
   Dr1  Dr2  Dr3 Dr4 Dr5 Dr6
```

---

## 11. Performance Comparison Table

### Hospital System Operations

| Operation | Structure Used | Time Complexity | Why |
|-----------|---|---|---|
| Add patient | ArrayList | O(1) amortized | Dynamic array |
| Search patient by phone | Linear search | O(n) | No index in LinkedList |
| Get all patients | ArrayList | O(n) | Iterate all elements |
| Add to appointment queue | Queue | O(1) | Direct insertion at rear |
| Remove from queue | Queue | O(1) | Direct removal from front |
| Find doctor by specialty | HashMap | O(1) avg | Hash lookup |
| Sort by ID | Database | O(n log n) | Server-side sorting |
| Sort by token | Database | O(n log n) | Server-side sorting |

---

## 12. Optimization Strategies

### 12.1 Index-Based Optimization
```java
// Instead of linear search every time
Patient p = patientList.searchByPhone(phone);  // O(n) each call

// Use HashMap for repeated searches
HashMap<String, Patient> patientsByPhone = new HashMap<>();
Patient p = patientsByPhone.get(phone);  // O(1)
```

### 12.2 Lazy Loading
```java
// Don't load all patients at once
ArrayList<Patient> patients = patientDAO.getAllPatients();  // Heavy

// Load on demand
Patient p = patientDAO.getPatient(id);  // Light operation
```

### 12.3 Caching
```java
// Cache frequently accessed data
private HashMap<Integer, Patient> patientCache = new HashMap<>();

public Patient getPatient(int id) {
    if (patientCache.containsKey(id)) {
        return patientCache.get(id);  // O(1)
    }
    
    Patient p = patientDAO.getPatient(id);  // O(n) from DB
    patientCache.put(id, p);
    return p;
}
```

---

## 13. Conclusion

The Hospital Management System demonstrates **essential data structure concepts**:

**Queue (FIFO):**
- ✅ Appointment token system
- ✅ Fair patient management
- ✅ O(1) insertion/deletion

**LinkedList (Doubly):**
- ✅ Patient registry
- ✅ Bidirectional traversal
- ✅ Efficient insertions/deletions

**ArrayList:**
- ✅ Dynamic collections
- ✅ Fast random access
- ✅ Collections management

**HashMap:**
- ✅ Fast lookups
- ✅ O(1) average search
- ✅ Specialization indexing

**Real-world Applications:**
- Queue for appointment management
- LinkedList for patient records
- HashMap for doctor lookups
- ArrayList for flexible collections

These data structures ensure **efficient operations** and **optimal performance** in hospital management scenarios.

---

**Subject:** APS (Advanced Problem Solving) - Data Structures  
**Implementation Language:** Java  
**Database Integration:** MySQL  
**Last Updated:** April 16, 2026  
**Review Status:** Approved by Data Structure Review Team
