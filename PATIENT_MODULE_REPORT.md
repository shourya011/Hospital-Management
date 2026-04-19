# AddPatient Module - Technical Report

**Project:** Hospital Management System (RBH)  
**Module:** Patient Management - Registration and Tracking  
**Date:** April 18, 2026  
**Status:** ✅ Fully Implemented and Tested  

---

## 📋 Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Database Schema](#database-schema)
4. [Module Components](#module-components)
5. [Features](#features)
6. [Code Structure](#code-structure)
7. [Integration with Observer Pattern](#integration-with-observer-pattern)
8. [Data Structures](#data-structures)
9. [Usage Guide](#usage-guide)
10. [Technical Specifications](#technical-specifications)
11. [Testing & Validation](#testing--validation)
12. [Future Enhancements](#future-enhancements)

---

## 1. Overview

The **AddPatient Module** is a comprehensive patient management interface built using Java Swing. It provides functionalities to register new patients, view all registered patients, search patients by phone number, and perform CRUD operations on patient records. The module also integrates custom data structures (PatientQueue and PatientLinkedList) for advanced data management.

### Key Objectives:
- ✅ Register new patients with complete medical information
- ✅ View all registered patients in a sortable table
- ✅ Search patients by phone number with live filtering
- ✅ Edit existing patient information
- ✅ Delete patients from the database
- ✅ Store patient data in custom data structures
- ✅ Synchronize data across all application modules in real-time

### Module Dependencies:
- `PatientDAO.java` - Data Access Object for database operations
- `Patient.java` - Entity model class
- `PatientQueue.java` - Queue data structure for FIFO processing
- `PatientLinkedList.java` - Linked list for sequential processing
- `DataChangeManager.java` - Observer pattern manager for real-time sync
- `DataChangeListener.java` - Interface for listening to data changes

---

## 2. Architecture

### Design Pattern: **MVC (Model-View-Controller) + Observer**

```
┌──────────────────────────────────────────────┐
│  AddPatientPanel (View/Controller)           │
│  - JPanel with form and table UI             │
│  - Handles user interactions                 │
│  - Manages validation                        │
└────────────┬──────────────────────────────┬──┘
             │                              │
             ├─→ PatientDAO                 │
             │   ├─ addPatient()            │
             │   ├─ getAllPatients()        │
             │   ├─ updatePatient()         │
             │   ├─ deletePatient()         │
             │   ├─ searchPatientByPhone()  │
             │   └─ searchPatientByName()   │
             │                              │
             ├─→ Patient.java (Entity)      │
             │   ├─ patientId               │
             │   ├─ firstName               │
             │   ├─ lastName                │
             │   └─ ... (other fields)      │
             │                              │
             ├─→ PatientQueue               ├─→ DataChangeManager
             │   ├─ enqueue()               │   (Observer notification)
             │   └─ dequeue()               │
             │                              │
             └─→ PatientLinkedList
                 ├─ add()
                 └─ traverse()
```

### Architectural Layers:

| Layer | Component | Responsibility |
|-------|-----------|-----------------|
| **UI Layer** | AddPatientPanel | Display form, table, handle interactions |
| **Business Logic** | PatientDAO | CRUD operations, database queries |
| **Data Model** | Patient | Patient entity with properties |
| **Data Structures** | PatientQueue, PatientLinkedList | In-memory data storage |
| **Observer** | DataChangeManager | Notify other panels of changes |
| **Database** | MySQL (hospital_db) | Persistent storage |

---

## 3. Database Schema

### Table: `patients`

```sql
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    blood_group VARCHAR(5),
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    pincode VARCHAR(10),
    registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_phone (phone),
    INDEX idx_first_name (first_name),
    INDEX idx_last_name (last_name)
);
```

### Fields Description:

| Field | Type | Constraint | Purpose |
|-------|------|-----------|---------|
| `patient_id` | INT | PK, AUTO_INCREMENT | Unique identifier |
| `first_name` | VARCHAR(50) | NOT NULL | Patient's first name |
| `last_name` | VARCHAR(50) | NOT NULL | Patient's last name |
| `date_of_birth` | DATE | NOT NULL | Date of birth for age calculation |
| `gender` | ENUM | NOT NULL | Male/Female/Other |
| `blood_group` | VARCHAR(5) | - | Blood group (A, B, AB, O) |
| `phone` | VARCHAR(15) | UNIQUE, NOT NULL | Contact phone number |
| `email` | VARCHAR(100) | - | Email address |
| `address` | TEXT | - | Residential address |
| `city` | VARCHAR(50) | - | City name |
| `state` | VARCHAR(50) | - | State/Province |
| `pincode` | VARCHAR(10) | - | Postal code |
| `registered_on` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Registration date |

### Indexes:
- `idx_phone` - Fast lookup by phone number
- `idx_first_name` - Fast search by first name
- `idx_last_name` - Fast search by last name

---

## 4. Module Components

### 4.1 AddPatientPanel.java

**Location:** `src/ui/AddPatientPanel.java`

**Class Definition:**
```java
public class AddPatientPanel extends JPanel
```

**Implements:** None  
**Inherits:** JPanel

**Key Fields:**
```java
private PatientDAO patientDAO;
private DataChangeManager dataChangeManager;
private PatientQueue patientQueue;
private PatientLinkedList patientLinkedList;

// Form fields
private JTextField firstNameField;
private JTextField lastNameField;
private JTextField dateOfBirthField;
private JComboBox<String> genderCombo;
private JComboBox<String> bloodGroupCombo;
private JTextField phoneField;
private JTextField emailField;
private JTextArea addressArea;
private JTextField cityField;
private JTextField stateField;
private JTextField pincodeField;

// Table and search
private JTable patientTable;
private DefaultTableModel tableModel;
private JTextField searchPhoneField;
```

**UI Components:**
- **Left Panel (40%):** Form for registering new patients
  - First name text field
  - Last name text field
  - Date of birth field (DD/MM/YYYY)
  - Gender dropdown
  - Blood group dropdown (A, B, AB, O, O+, O-)
  - Phone number text field
  - Email text field
  - Address text area
  - City text field
  - State text field
  - Pincode text field
  - Register and Clear buttons

- **Right Panel (60%):** Patient list table
  - Search by phone panel with live filtering
  - 12-column table (ID, First Name, Last Name, DOB, Gender, Blood Group, Phone, Email, Address, City, State, Pincode)
  - Edit and Delete buttons
  - Refresh button

### 4.2 PatientDAO.java

**Location:** `src/dao/PatientDAO.java`

**Methods:**

#### Create Operations
```java
public boolean addPatient(Patient patient)
```
- Inserts new patient into database
- **Parameters:** Patient object with all details
- **Returns:** true if successful, false otherwise

#### Read Operations
```java
public ArrayList<Patient> getAllPatients()
public Patient getPatientById(int patientId)
public Patient searchPatientByPhone(String phone)
public ArrayList<Patient> searchPatientByFirstName(String firstName)
public ArrayList<Patient> searchPatientByLastName(String lastName)
public int getTotalPatients()
```

#### Update Operations
```java
public boolean updatePatient(Patient patient)
```
- Updates existing patient information
- **Parameters:** Patient object with updated values
- **Returns:** true if successful, false otherwise

#### Delete Operations
```java
public boolean deletePatient(int patientId)
```
- Deletes patient from database
- **Parameters:** patient_id
- **Returns:** true if successful, false otherwise

### 4.3 Patient.java

**Location:** `src/model/Patient.java`

**Class Definition:**
```java
public class Patient
```

**Constructors:**
```java
public Patient()  // Default constructor

public Patient(String firstName, String lastName, LocalDate dateOfBirth,
               String gender, String bloodGroup, String phone, String email,
               String address, String city, String state, String pincode)  // For registration

public Patient(int patientId, String firstName, String lastName, LocalDate dateOfBirth,
               String gender, String bloodGroup, String phone, String email,
               String address, String city, String state, String pincode)  // With ID
```

**Key Methods:**
```java
// Getters
public int getPatientId()
public String getFirstName()
public String getLastName()
public LocalDate getDateOfBirth()
public String getGender()
public String getBloodGroup()
public String getPhone()
public String getEmail()
public String getAddress()
public String getCity()
public String getState()
public String getPincode()

// Setters and utility methods
public void setPatientId(int patientId)
public void setFirstName(String firstName)
// ... and other setters
public String getFullName()  // Combines first and last name
```

### 4.4 PatientQueue.java

**Location:** `src/datastructure/PatientQueue.java`

**Data Structure:** FIFO (First In First Out) Queue

**Key Methods:**
```java
public void enqueue(Patient patient)    // Add patient to queue
public Patient dequeue()                // Remove first patient
public Patient peek()                   // View first patient without removal
public boolean isEmpty()                // Check if queue is empty
public int size()                       // Get number of patients
public void display()                   // Print all patients in queue
```

**Purpose:** Manages patient queue for appointment scheduling and FIFO processing

### 4.5 PatientLinkedList.java

**Location:** `src/datastructure/PatientLinkedList.java`

**Data Structure:** Singly Linked List

**Key Methods:**
```java
public void add(Patient patient)        // Add patient to list
public void remove(int patientId)       // Remove patient by ID
public Patient search(int patientId)    // Find patient by ID
public void display()                   // Print all patients
public int size()                       // Get number of patients
public boolean isEmpty()                // Check if list is empty
```

**Purpose:** Maintains ordered patient data for sequential processing and searches

---

## 5. Features

### 5.1 Patient Registration
- **Form Validation:** All required fields must be filled
- **Input Fields:**
  - Name (First and Last)
  - Date of Birth (DD/MM/YYYY format)
  - Gender (Male/Female/Other)
  - Blood Group (A, B, AB, O with +/- modifiers)
  - Contact Information (Phone, Email)
  - Address (Full address, City, State, Pincode)
- **Phone Uniqueness:** Database ensures no duplicate phone numbers
- **Age Calculation:** Automatic from date of birth

### 5.2 Patient Listing
- **Table Display:** 12 columns with sortable headers
- **Auto-refresh:** Table updates when data changes
- **Column Headers:** ID, First Name, Last Name, DOB, Gender, Blood Group, Phone, Email, Address, City, State, Pincode
- **Row Height:** 25 pixels for readability
- **Data Persistence:** Links with PatientQueue and PatientLinkedList

### 5.3 Search Functionality
- **Search Types:**
  - By phone number (primary)
  - By first name
  - By last name
- **Search Trigger:**
  - Real-time search (on key release)
  - Search button click
- **No Results Handling:** Shows message dialog, reloads all patients

### 5.4 Edit Functionality
- **Trigger:** Select row and click EDIT button
- **Behavior:**
  - Populates form with selected patient's data
  - Shows confirmation dialog
  - Updates database and table on confirmation
  - Stores in data structures

### 5.5 Delete Functionality
- **Trigger:** Select row and click DELETE button
- **Confirmation:** Shows dialog asking for confirmation
- **Behavior:** 
  - Removes patient from database
  - Removes from PatientQueue and PatientLinkedList
  - Updates table display
  - Cascades to appointments (if any)

### 5.6 Real-time Synchronization
- **Observer Pattern:** Uses DataChangeManager
- **Notifications Sent On:**
  - Patient added
  - Patient updated
  - Patient deleted
- **Listeners Notified:**
  - AppointmentPanel (validates patient selection)
  - Any other panel implementing DataChangeListener

---

## 6. Code Structure

### File Organization:
```
src/
├── ui/
│   ├── AddPatientPanel.java         (Main UI component)
│   ├── DataChangeListener.java      (Observer interface)
│   ├── DataChangeManager.java       (Singleton event bus)
│   └── MainDashboard.java           (Integration)
├── dao/
│   └── PatientDAO.java              (Data access)
├── model/
│   └── Patient.java                 (Entity class)
├── datastructure/
│   ├── PatientQueue.java            (FIFO queue)
│   └── PatientLinkedList.java       (Linked list)
└── db/
    └── DBConnection.java            (Connection pool)
```

### Method Organization in AddPatientPanel:

| Method | Purpose |
|--------|---------|
| `public AddPatientPanel(PatientDAO patientDAO)` | Constructor |
| `private JPanel createTitlePanel()` | Create header |
| `private JPanel createFormPanel()` | Create form section |
| `private JPanel createTablePanel()` | Create table section |
| `private void loadPatients()` | Load all from DB |
| `private void searchPatients()` | Filter by phone |
| `private void registerPatient()` | Add new patient |
| `private void editSelectedPatient()` | Update patient |
| `private void deleteSelectedPatient()` | Remove patient |
| `private void clearForm()` | Reset form fields |
| `private boolean validateForm()` | Validate inputs |
| `private JLabel createFieldLabel(String)` | Create label |
| `private JPanel createLabeledField(...)` | Create field with label |

---

## 7. Integration with Observer Pattern

### Observer Pattern Flow:

```
User registers new patient in AddPatientPanel
    ↓
registerPatient() method executes
    ↓
patientDAO.addPatient() succeeds
    ↓
Patient added to PatientQueue (enqueue)
Patient added to PatientLinkedList (add)
    ↓
clearForm() and loadPatients() called
    ↓
dataChangeManager.notifyPatientDataChanged() called
    ↓
All registered listeners notified:
    └─→ AppointmentPanel.onPatientDataChanged()
        └─→ Validates current patient selection
```

### Classes Implementing DataChangeListener:
1. **AppointmentPanel** - Validates selected patient
2. **Any future modules** that depend on patient data

### Notification Points in AddPatientPanel:
```java
// After successful patient addition
dataChangeManager.notifyPatientDataChanged();
```

---

## 8. Data Structures

### PatientQueue (FIFO)

**Purpose:** Manage patients in first-come-first-served order for processing

**Operations:**
```java
// Enqueue when patient is added
patientQueue.enqueue(savedPatient);

// Dequeue when patient is processed
Patient nextPatient = patientQueue.dequeue();

// Check next without removing
Patient nextInLine = patientQueue.peek();
```

**Use Cases:**
- Appointment queue management
- Patient waiting list processing
- FIFO-based patient registration tracking

### PatientLinkedList

**Purpose:** Maintain ordered patient data with efficient insertion/deletion

**Operations:**
```java
// Add patient to linked list
patientLinkedList.add(savedPatient);

// Search by patient ID
Patient found = patientLinkedList.search(patientId);

// Remove patient
patientLinkedList.remove(patientId);
```

**Use Cases:**
- Sequential patient record traversal
- Efficient search without database calls
- Patient data aggregation
- Report generation

---

## 9. Usage Guide

### For End Users:

#### Registering a Patient:
1. Click "👥 Patients" button in sidebar
2. Fill in patient details in the left form:
   - First Name (required)
   - Last Name (required)
   - Date of Birth (DD/MM/YYYY format, required)
   - Gender (dropdown: Male/Female/Other)
   - Blood Group (dropdown: A, B, AB, O with +/-)
   - Phone Number (required, unique)
   - Email address
   - Address (required)
   - City, State, Pincode
3. Click "REGISTER PATIENT" button
4. Success message appears
5. Form clears and table refreshes
6. Patient automatically available for appointment booking

#### Viewing All Patients:
- Table displays all registered patients
- Click column headers to sort (ID, Name, DOB, Gender, Phone, etc.)
- 12 columns show complete patient information

#### Searching for a Patient:
1. Enter phone number in search field
2. Click "Search" or it searches automatically (on key release)
3. Table shows matching patient(s)
4. Click "Refresh" to show all patients again

#### Editing a Patient:
1. Select patient row in table
2. Click "EDIT" button
3. Form populates with patient's data
4. Modify any field (except patient ID)
5. Click confirmation dialog "YES"
6. Changes saved and table updates

#### Deleting a Patient:
1. Select patient row in table
2. Click "DELETE" button
3. Confirmation dialog appears
4. Click "YES" to confirm deletion
5. Patient removed from database and data structures
6. Any linked appointments affected

### For Developers:

#### Integration with MainDashboard:
```java
// In MainDashboard constructor:
PatientDAO patientDAO = new PatientDAO();
AddPatientPanel addPatientPanel = new AddPatientPanel(patientDAO);

// Add to CardLayout
cardPanel.add(addPatientPanel, "addPatient");

// Add button listener
addPatientBtn.addActionListener(e -> switchPanel("addPatient"));
```

#### Using PatientDAO:
```java
PatientDAO dao = new PatientDAO();

// Add patient
Patient patient = new Patient("John", "Doe", LocalDate.of(1990, 5, 15),
                             "Male", "O+", "9876543210", "john@email.com",
                             "123 Main St", "New York", "NY", "10001");
dao.addPatient(patient);

// Get all patients
ArrayList<Patient> patients = dao.getAllPatients();

// Search by phone
Patient found = dao.searchPatientByPhone("9876543210");

// Update patient
patient.setEmail("newemail@email.com");
dao.updatePatient(patient);

// Delete patient
dao.deletePatient(patientId);
```

#### Using Data Structures:
```java
PatientQueue queue = new PatientQueue();
PatientLinkedList list = new PatientLinkedList();

// Add to both
Patient patient = // ... get patient
queue.enqueue(patient);
list.add(patient);

// Process from queue
Patient next = queue.dequeue();

// Search in list
Patient found = list.search(patientId);
```

---

## 10. Technical Specifications

### UI Specifications:

| Aspect | Value |
|--------|-------|
| **Primary Font** | Segoe UI |
| **Title Font Size** | 22pt Bold |
| **Label Font Size** | 10-11pt |
| **Field Font Size** | 11pt |
| **Button Font Size** | 10-11pt |
| **Dark Blue Color** | RGB(0, 53, 102) |
| **Light Gray Color** | RGB(240, 240, 240) |
| **Red Accent Color** | RGB(214, 40, 40) |
| **White Color** | RGB(255, 255, 255) |
| **Form Width** | 400px (fixed) |
| **Table Width** | 600px (min) |
| **Row Height** | 25px |
| **Button Height** | 30-38px |

### Blood Group Options:
```
A, A+, A-, B, B+, B-, AB, AB+, AB-, O, O+, O-
```

### Gender Options:
```
Male, Female, Other
```

### Data Validation:

| Field | Validation | Error Message |
|-------|-----------|---------------|
| First Name | Non-empty | "Please fill in all required fields!" |
| Last Name | Non-empty | "Please fill in all required fields!" |
| Date of Birth | DD/MM/YYYY format | "Invalid date format. Use dd/MM/yyyy" |
| Gender | Dropdown selection | Auto-selected |
| Blood Group | Dropdown selection | Auto-selected |
| Phone | Non-empty, unique | "Please fill in all required fields!" |
| Address | Non-empty | "Please fill in all required fields!" |

---

## 11. Testing & Validation

### Unit Testing Scenarios:

#### Test 1: Register Patient
```
Input: Valid patient details
Expected: Patient saved, stored in queue and list, table refreshed
Result: ✅ PASS
```

#### Test 2: Search Patient
```
Input: Existing phone number
Expected: Table shows matching patient
Result: ✅ PASS
```

#### Test 3: Edit Patient
```
Input: Modify patient email
Expected: Changes saved, table updated, data structures updated
Result: ✅ PASS
```

#### Test 4: Delete Patient
```
Input: Delete patient with appointments
Expected: Patient deleted, cascade handled, all structures updated
Result: ✅ PASS
```

#### Test 5: Data Structure Integration
```
Action: Register patient
Expected: Added to PatientQueue, PatientLinkedList, and database
Result: ✅ PASS
```

#### Test 6: Real-time Sync
```
Action: Register patient, switch to Appointment
Expected: Patient available in appointment booking
Result: ✅ PASS
```

---

## 12. Future Enhancements

### Planned Features:

#### Phase 2:
- [ ] **Medical History:** Track patient medical conditions
- [ ] **Allergies:** Record medication allergies
- [ ] **Insurance:** Store insurance details
- [ ] **Emergency Contact:** Primary and secondary contacts
- [ ] **Photo Upload:** Patient identification photos

#### Phase 3:
- [ ] **Medical Records:** Attach previous medical reports
- [ ] **Vaccination History:** Track immunizations
- [ ] **Family History:** Record hereditary conditions
- [ ] **Pharmacy Integration:** Prescription tracking
- [ ] **Lab Results:** Link to lab test results

#### Phase 4:
- [ ] **Patient Portal:** Self-service appointment booking
- [ ] **Analytics Dashboard:** Patient demographics analysis
- [ ] **Export Functionality:** Generate patient reports
- [ ] **Patient Communication:** Messages and notifications
- [ ] **Advanced Search:** Filter by multiple criteria

---

## 13. Conclusion

The **AddPatient Module** is a comprehensive, well-structured component of the Hospital Management System that provides complete patient management capabilities. With real-time synchronization through the Observer pattern and integration with custom data structures, it ensures robust patient data management across the application.

### Key Achievements:
✅ Full CRUD operations for patients  
✅ Real-time multi-module synchronization  
✅ Custom data structure integration (Queue & LinkedList)  
✅ Comprehensive form validation  
✅ Fast search and filter capabilities  
✅ Professional UI matching system design  
✅ Secure database operations  
✅ Production-ready implementation  

### Maintenance Notes:
- Database connection handled by singleton DBConnection
- All SQL queries use PreparedStatements (SQL injection safe)
- Observer pattern prevents tight coupling between modules
- Code follows MVC pattern for easy maintenance
- Data structures provide in-memory caching for performance
- Comprehensive error handling with user-friendly messages

---

**Report Generated:** April 18, 2026  
**Module Status:** ✅ Production Ready  
**Last Updated:** v1.0.0
