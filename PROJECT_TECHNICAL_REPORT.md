# Hospital Management System - Technical Report

**Project Name:** Birla CK Hospital - Hospital Management System  
**Version:** 1.1  
**Date:** April 23, 2026  
**Developed By:** Shourya Sharma (with team contributions)  
**Repository:** https://github.com/shourya011/Hospital-Management

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Java Swing - GUI Framework](#java-swing---gui-framework)
3. [MySQL - Database Layer](#mysql---database-layer)
4. [Data Structures](#data-structures)
5. [System Architecture](#system-architecture)
6. [Modules & Features](#modules--features)
7. [Technology Stack](#technology-stack)
8. [Database Schema](#database-schema)
9. [Future Enhancements](#future-enhancements)

---

## Project Overview

The **Hospital Management System** is a comprehensive Java-based desktop application designed to manage hospital operations including patient management, doctor records, appointment scheduling, billing, and reporting. The system uses **Java Swing** for the graphical user interface, **MySQL** for persistent data storage, and custom **data structures** for efficient data manipulation.

### Key Objectives:
- ✅ Centralized patient management
- ✅ Appointment scheduling and tracking
- ✅ Doctor and staff management
- ✅ Billing and payment processing
- ✅ Report generation and analytics

---

## Java Swing - GUI Framework

### Overview
Java Swing is a lightweight, platform-independent GUI toolkit used to build the user interface for the Hospital Management System. It provides rich components and allows for custom layouts and styling.

### Components Used

#### 1. **Main Dashboard (MainDashboard.java)**
The primary application window featuring:

```
┌─────────────────────────────────────────────────────────────────┐
│  [Birla CK Hospital Logo] ▸ Dashboard      [Thu, Apr 23, 2026] │  ← Header (Primary Red)
├────────────────────────────────────────────────────────────────┤
│ ┌─────────┬──────────────────────────────────────────────────┐ │
│ │ SIDEBAR │           CARD LAYOUT - Current Panel            │ │
│ │────────│                                                   │ │
│ │📊 Dash │  [Dynamic Content Area - Swaps based on selection]│ │
│ │➕ Add  │                                                   │ │
│ │📅 Appt │                                                   │ │
│ │👨‍⚕️ Docs│                                                   │ │
│ │💰 Bill │                                                   │ │
│ │📈 Rept │                                                   │ │
│ │🚪 Exit │                                                   │ │
│ └─────────┴──────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

**Key Features:**
- **CardLayout:** Enables seamless switching between different modules
- **BorderLayout:** Organizes header, sidebar, and content areas
- **Navigation Buttons:** Color-coded buttons for different modules
- **Real-time Clock:** Updates date and time in the header

#### 2. **UI Panels & Managers**

| Panel | Purpose | Features |
|-------|---------|----------|
| `DashboardPanel` | Home screen statistics | Overview of key metrics |
| `AddPatientPanel` | Patient registration | Form-based data entry |
| `AppointmentPanel` | Appointment scheduling | Date picker and doctor selection |
| `DoctorListPanel` | Doctor management | CRUD operations for doctors |
| `BillingPanel` | Invoice generation | Charge breakdown and payment tracking |
| `ReportPanel` | Analytics dashboard | Data visualization |

#### 3. **Swing Components Architecture**

```java
JFrame (MainDashboard)
├── JPanel (Top Header)
│   ├── JLabel (Logo/Hospital Name)
│   ├── JLabel (Current Module)
│   └── JLabel (Date/Time)
├── JPanel (Sidebar Navigation)
│   ├── JPanel (Logo Brand Panel)
│   └── JButton[] (Navigation Buttons)
└── JPanel (Content - CardLayout)
    ├── DashboardPanel
    ├── AddPatientPanel
    ├── AppointmentPanel
    ├── DoctorListPanel
    ├── BillingPanel
    └── ReportPanel
```

#### 4. **Color Scheme & Styling**

```
PRIMARY_RED       = #C8102E    (Main branding color)
DARK_GRAY         = #4A4A4A    (Text and headings)
STEEL_GRAY        = #607D8B    (Secondary elements)
LIGHT_GRAY        = #F5F5F5    (Background)
ACCENT_PINK       = #FFEBEE    (Highlight areas)
HOVER_RED         = #9B0D22    (Button hover state)
```

#### 5. **Event Handling**
- **ActionListener:** Button clicks for navigation
- **DataChangeListener:** Real-time updates across panels
- **Timer:** Automatic date/time updates
- **Lambda Expressions:** Modern event handling

#### 6. **Image Loading**
- Loads `rbh.webp` logo dynamically
- Falls back to styled "Birla CK Hospital" text
- Scales images to fit header height (45px)

---

## MySQL - Database Layer

### Database Configuration
**Database Name:** `hospital_db`  
**Driver:** MySQL Connector/J 9.6.0  
**Connection String:** `jdbc:mysql://127.0.0.1:3306/hospital_db`

### Connection Management (DBConnection.java)

```java
// Singleton Pattern for Database Connection
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;
    
    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
}
```

**Features:**
- ✅ Connection pooling for efficiency
- ✅ Property file configuration (db.properties)
- ✅ Singleton pattern for thread-safety
- ✅ Error handling and logging

### Core Database Tables

#### 1. **Patients Table**
```sql
CREATE TABLE patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('Male', 'Female', 'Other'),
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

**Purpose:** Store patient demographic information  
**Records:** Sample data for 5+ patients  
**Indexing:** Phone, first name, last name for quick searches

#### 2. **Doctors Table**
```sql
CREATE TABLE doctors (
    doctor_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    gender VARCHAR(20),
    experience_years INT,
    availability TEXT,
    status VARCHAR(20),
    registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_specialization (specialization),
    INDEX idx_phone_number (phone_number)
);
```

**Purpose:** Maintain doctor profiles and credentials  
**Features:** Specialization filtering, availability tracking

#### 3. **Appointments Table**
```sql
CREATE TABLE appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason TEXT,
    status ENUM('Scheduled', 'Completed', 'Cancelled'),
    token_number INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_status (status)
);
```

**Purpose:** Track patient-doctor appointments  
**Features:** Token number system for queue management

#### 4. **Bills Table** (NEW - v1.1)
```sql
CREATE TABLE bills (
    bill_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    appointment_id INT DEFAULT NULL,
    bill_number VARCHAR(30) NOT NULL UNIQUE,
    consultation_fee DECIMAL(10,2),
    medicine_charges DECIMAL(10,2),
    room_charges DECIMAL(10,2),
    lab_charges DECIMAL(10,2),
    other_charges DECIMAL(10,2),
    discount DECIMAL(10,2),
    tax DECIMAL(10,2),
    total_amount DECIMAL(10,2),
    payment_status ENUM('Pending', 'Paid', 'Partially Paid'),
    payment_mode ENUM('Cash', 'Card', 'UPI', 'Insurance'),
    bill_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    INDEX idx_bill_patient (patient_id),
    INDEX idx_bill_status (payment_status),
    INDEX idx_bill_date (bill_date)
);
```

**Purpose:** Billing and financial tracking  
**Features:** Automatic bill number generation (RBH-BILL-YYYYMMDD-XXXX), flexible payment modes

### Data Access Objects (DAO Pattern)

Each table has a dedicated DAO class using **JDBC PreparedStatements**:

| DAO Class | Operations |
|-----------|-----------|
| `PatientDAO` | Create, Read, Update, Delete patients |
| `DoctorDAO` | CRUD for doctors |
| `AppointmentDAO` | Schedule, update, cancel appointments |
| `BillDAO` | Generate bills, process payments |
| `ReportDAO` | Generate analytics reports |

**Example DAO Method:**
```java
public boolean createBill(Bill bill) {
    String sql = "INSERT INTO bills (...) VALUES (?, ?, ?, ...)";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, bill.getPatientId());
        pstmt.setString(3, bill.getBillNumber());
        // ... more parameter binding
        return pstmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
```

### Database Performance Optimizations

1. **Indexes:** Created on frequently searched columns (phone, date, status)
2. **Foreign Keys:** Maintain referential integrity
3. **PreparedStatements:** Prevent SQL injection and improve performance
4. **Connection Reuse:** Singleton pattern for shared connection

---

## Data Structures

### Custom Data Structures Implemented

#### 1. **PatientLinkedList** (src/datastructure/PatientLinkedList.java)

**Purpose:** Maintain ordered patient records in memory  
**Implementation:** Singly Linked List

```
Head → [Patient1] → [Patient2] → [Patient3] → ... → Tail
```

**Key Operations:**
```java
public void insert(Patient patient)      // O(n) - sorted insertion
public void delete(int patientId)        // O(n) - search and remove
public Patient search(int patientId)     // O(n) - linear search
public void display()                    // O(n) - print all patients
```

**Use Cases:**
- Maintaining sorted patient records by ID
- Quick iteration without database queries
- In-memory caching for display

#### 2. **PatientQueue** (src/datastructure/PatientQueue.java)

**Purpose:** Manage appointment queue (FIFO)  
**Implementation:** Circular Queue

```
[Patient A] [Patient B] [Patient C] [Patient D]
Front ↑                              ↑ Rear
```

**Key Operations:**
```java
public void enqueue(Patient patient)     // Add to end (O(1))
public Patient dequeue()                 // Remove from front (O(1))
public Patient peek()                    // View front (O(1))
public boolean isEmpty()                 // Check empty (O(1))
public int size()                        // Get queue size (O(1))
```

**Use Cases:**
- Token-based appointment system
- Queue management for doctor visits
- Fair scheduling (FIFO)

#### 3. **ReportDataList** (src/datastructure/ReportDataList.java)

**Purpose:** Store and process report data  
**Implementation:** Dynamic Array/ArrayList

```java
ArrayList<ReportRow> reportData = new ArrayList<>();
```

**Structure:**
```
[ReportRow 1: {patientName, date, charges, status}]
[ReportRow 2: {patientName, date, charges, status}]
[ReportRow 3: {patientName, date, charges, status}]
...
```

**Key Operations:**
```java
public void add(ReportRow row)           // Append (O(1) amortized)
public ReportRow get(int index)          // Access by index (O(1))
public void remove(int index)            // Delete by index (O(n))
public int size()                        // Get count (O(1))
public void sort(Comparator comp)        // Sort data (O(n log n))
public double calculateTotal()           // Aggregate (O(n))
```

**Use Cases:**
- Storing query results for reports
- Aggregating financial data
- Generating summaries and statistics

### Data Structure Performance Analysis

| Data Structure | Insert | Delete | Search | Space |
|---------------|--------|--------|--------|-------|
| PatientLinkedList | O(n) | O(n) | O(n) | O(n) |
| PatientQueue | O(1) | O(1) | O(n) | O(n) |
| ReportDataList | O(1)* | O(n) | O(n) | O(n) |

*Amortized time complexity

### Data Model Classes (src/model/)

```
Patient
├── patientId: int
├── firstName: String
├── lastName: String
├── dateOfBirth: Date
├── gender: String
├── bloodGroup: String
├── phone: String
├── email: String
├── address: String
├── city: String
├── state: String
└── pincode: String

Doctor
├── doctorId: int
├── doctorName: String
├── specialization: String
├── phoneNumber: String
├── email: String
├── gender: String
├── experienceYears: int
├── availability: String
└── status: String

Appointment
├── appointmentId: int
├── patientId: int
├── doctorId: int
├── appointmentDate: Date
├── appointmentTime: Time
├── reason: String
├── status: String (Enum)
└── tokenNumber: int

Bill
├── billId: int
├── patientId: int
├── appointmentId: int
├── billNumber: String
├── consultationFee: Double
├── medicineCharges: Double
├── roomCharges: Double
├── labCharges: Double
├── otherCharges: Double
├── discount: Double
├── tax: Double
├── totalAmount: Double
├── paymentStatus: Enum
├── paymentMode: Enum
└── notes: String

ReportRow
├── reportId: int
├── patientName: String
├── date: Date
├── chargeType: String
├── amount: Double
└── status: String
```

---

## System Architecture

### Three-Layer Architecture

```
┌─────────────────────────────────────────┐
│     PRESENTATION LAYER (GUI)            │
│  ┌─────────────────────────────────┐   │
│  │ Java Swing Components           │   │
│  │ - Panels, Buttons, Tables       │   │
│  │ - Event Handlers, Listeners     │   │
│  └─────────────────────────────────┘   │
└──────────────┬──────────────────────────┘
               │ (Calls DAO methods)
┌──────────────▼──────────────────────────┐
│     BUSINESS LOGIC LAYER                │
│  ┌─────────────────────────────────┐   │
│  │ DAO Classes                     │   │
│  │ - PatientDAO, DoctorDAO         │   │
│  │ - AppointmentDAO, BillDAO       │   │
│  │ - ReportDAO                     │   │
│  └─────────────────────────────────┘   │
│  ┌─────────────────────────────────┐   │
│  │ Data Structures                 │   │
│  │ - PatientLinkedList, Queue      │   │
│  │ - ReportDataList                │   │
│  └─────────────────────────────────┘   │
└──────────────┬──────────────────────────┘
               │ (JDBC Queries)
┌──────────────▼──────────────────────────┐
│     DATA LAYER (MySQL)                  │
│  ┌─────────────────────────────────┐   │
│  │ Database Tables                 │   │
│  │ - patients, doctors             │   │
│  │ - appointments, bills           │   │
│  │ - reports                       │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

### Design Patterns Used

1. **Singleton Pattern:** `DBConnection` - Single database instance
2. **DAO Pattern:** Separate data access logic from business logic
3. **MVC Pattern:** Model (data classes), View (Swing panels), Controller (DAO)
4. **Observer Pattern:** `DataChangeListener` for real-time updates
5. **CardLayout:** Dynamic panel switching

---

## Modules & Features

### 1. **Dashboard Module**
- Overview statistics (total patients, appointments, doctors)
- Quick access buttons to other modules
- Real-time information display

### 2. **Patient Management**
- Register new patients
- View patient records
- Update patient information
- Delete patient records
- Phone-based search functionality

### 3. **Appointment Scheduling**
- Schedule appointments with doctors
- Select date and time
- Track appointment status
- Token-based queue system

### 4. **Doctor Management**
- Maintain doctor profiles
- Specialization categories
- Availability tracking
- Doctor search and filtering

### 5. **Billing Module** (v1.1)
- Generate bills automatically
- Itemized charge breakdown:
  - Consultation fees
  - Medicine charges
  - Room charges
  - Lab charges
  - Other charges
- Discount application
- Tax calculation
- Payment status tracking (Pending, Paid, Partially Paid)
- Multiple payment modes (Cash, Card, UPI, Insurance)
- Automatic bill number generation: `RBH-BILL-YYYYMMDD-XXXX`

### 6. **Report Generation**
- Financial reports
- Patient statistics
- Appointment analytics
- Export data capabilities

---

## Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **GUI** | Java Swing | Built-in (Java 11+) |
| **Backend** | Java | 11+ (OpenJDK) |
| **Database** | MySQL | 5.7+ |
| **Driver** | MySQL Connector/J | 9.6.0 |
| **Build** | Bash Scripts | Automated compilation |
| **Version Control** | Git | GitHub hosted |

### Compilation & Execution

**Compile:**
```bash
./compile.sh
```

**Run:**
```bash
java -cp "bin:lib/mysql-connector-j-9.6.0.jar" Main
```

**Configuration File:** `db.properties`
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://127.0.0.1:3306/hospital_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
db.user=root
db.password=shourya@123
```

---

## Database Schema

### Entity Relationship Diagram (ERD)

```
┌──────────────┐         ┌──────────────┐
│  Patients    │         │   Doctors    │
├──────────────┤         ├──────────────┤
│ patient_id ●─├─────────┤─● doctor_id  │
│ first_name   │    1:M   │ doctor_name  │
│ last_name    │         │ specialization
│ phone        │         │ phone_number │
│ email        │         │ email        │
│ address      │         │ experience   │
└──────────────┘         └──────────────┘
       ▲                        ▲
       │ 1                      │ 1
       │                        │
       │ M                      │ M
       │                        │
┌──────┴────────────────────────┴──┐
│       Appointments             │
├────────────────────────────────┤
│ appointment_id                 │
│ patient_id ● (FK)             │
│ doctor_id ● (FK)              │
│ appointment_date              │
│ appointment_time              │
│ status                         │
│ token_number                   │
└──────────────────────────────┬─┘
                               │
                               │ 1:M
                               │
                        ┌──────▼────────┐
                        │    Bills      │
                        ├───────────────┤
                        │ bill_id       │
                        │ patient_id ●  │
                        │ appointment_id│
                        │ bill_number   │
                        │ total_amount  │
                        │ payment_status│
                        │ payment_mode  │
                        └───────────────┘
```

### Key Constraints

1. **Referential Integrity:** Foreign keys enforce relationships
2. **Unique Constraints:** Bill numbers, phone numbers are unique
3. **Not Null:** Core fields are non-nullable
4. **Check Constraints:** Enum fields restrict valid values
5. **Cascade Delete:** Deleting a patient removes their appointments

---

## Testing & Validation

### Test Data
- 5+ sample patients with diverse demographics
- 5+ doctors with different specializations
- Sample appointments and bills
- Various payment statuses

### Error Handling
- ✅ Database connection failures
- ✅ Invalid input validation
- ✅ Duplicate phone number detection
- ✅ SQL exception handling
- ✅ GUI error dialogs

---

## Performance Metrics

### Database Performance
- **Connection Pool:** Reuses single connection (Singleton)
- **Indexing:** O(log n) for indexed searches
- **Query Optimization:** PreparedStatements reduce parsing overhead

### GUI Performance
- **CardLayout:** Instant panel switching (no reloads)
- **Real-time Updates:** Event-driven architecture
- **Memory Efficient:** Data structures avoid redundant storage

---

## Future Enhancements

### Planned Features (v1.2+)
- [ ] User authentication & role-based access control
- [ ] Prescription management module
- [ ] Inventory management for medicines
- [ ] Multi-language support
- [ ] Mobile app integration (REST API)
- [ ] Advanced reporting with charts & graphs
- [ ] Appointment reminders (SMS/Email)
- [ ] Doctor performance analytics
- [ ] Patient medical history tracking
- [ ] Insurance claim processing
- [ ] Data backup & recovery system
- [ ] Cloud database migration (AWS RDS)

### Technical Improvements
- [ ] Migrate to Swing → JavaFX/Spring Boot Web UI
- [ ] Implement connection pooling (HikariCP)
- [ ] Add logging framework (Log4j)
- [ ] Unit testing suite (JUnit, Mockito)
- [ ] API documentation (JavaDoc)
- [ ] Performance monitoring tools

---

## Conclusion

The **Hospital Management System** demonstrates a well-structured three-layer architecture combining:

1. **Java Swing** for professional, responsive GUI with intuitive navigation
2. **MySQL** with proper schema design, indexing, and referential integrity
3. **Custom Data Structures** for efficient in-memory data manipulation

The project successfully implements CRUD operations, real-time updates, and complex business logic while maintaining clean code and separation of concerns. The billing module (v1.1) introduces financial tracking capabilities, making this a comprehensive solution for hospital administration.

---

## Project Statistics

| Metric | Value |
|--------|-------|
| Total Java Files | 18+ |
| Lines of Code | 2000+ |
| Database Tables | 4 |
| Custom Data Structures | 3 |
| UI Panels | 6 |
| DAO Classes | 5 |
| Git Commits | 15+ |

---

**Last Updated:** April 23, 2026  
**Maintainer:** Shourya Sharma  
**Repository:** https://github.com/shourya011/Hospital-Management

---

## License & Attribution

This project is part of a learning initiative demonstrating:
- Java Swing GUI development
- Database design and JDBC programming
- Data structure implementation
- Software architecture patterns

For questions or contributions, visit the GitHub repository.
