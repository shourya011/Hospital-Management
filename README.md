# RBH Hospital Management System

A complete Hospital Management System desktop application built with Java Swing and MySQL.

## Features

- **Patient Management**: Register and manage patient information
- **Appointment Booking**: Schedule appointments with doctors
- **Doctor Directory**: Access doctor information and specializations
- **Appointment Status Tracking**: Track appointment status with token numbers
- **Data Structures**: Uses LinkedList and Queue for efficient data handling
- **Database Integration**: Full MySQL integration with JDBC

## Project Structure

```
HospitalManagementSystem/
├── src/
│   ├── db/
│   │   └── DBConnection.java          # Singleton DB connection
│   ├── model/
│   │   ├── Patient.java               # Patient entity
│   │   └── Appointment.java           # Appointment entity
│   ├── dao/
│   │   ├── PatientDAO.java            # CRUD for Patient
│   │   └── AppointmentDAO.java        # CRUD for Appointment
│   ├── datastructure/
│   │   ├── PatientQueue.java          # Queue using LinkedList
│   │   └── PatientLinkedList.java     # Custom LinkedList
│   ├── ui/
│   │   ├── MainDashboard.java         # Main window
│   │   ├── AddPatientPanel.java       # Patient registration panel
│   │   └── AppointmentPanel.java      # Appointment booking panel
│   └── Main.java                      # Entry point
├── lib/
│   └── mysql-connector-java-8.x.jar   # MySQL JDBC driver
└── hospital_db.sql                    # Database schema
```

## Requirements

- **Java 11+**
- **MySQL 8.x**
- **MySQL Connector/J (JDBC Driver)**

## Setup Instructions

### 1. Install MySQL

Make sure MySQL server is installed and running on your system.

### 2. Import Database Schema

```bash
mysql -u root -p < hospital_db.sql
```

When prompted, enter your MySQL password. The database `hospital_db` will be created automatically.

### 3. Download MySQL Connector JAR

1. Download `mysql-connector-java-8.x.jar` from [MySQL Downloads](https://dev.mysql.com/downloads/connector/j/)
2. Place the JAR file in the `lib/` directory of the project

### 4. Configure Database Credentials

Database credentials are stored in `db.properties` (not tracked in git for security):

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://127.0.0.1:3306/hospital_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD
```

**Important**: Create a `db.properties` file in the project root with your actual MySQL credentials. The file is gitignored and will not be uploaded to GitHub.

## Compilation Instructions

### Using Command Line

1. **Navigate to project directory**:
```bash
cd /path/to/HospitalManagementSystem
```

2. **Set CLASSPATH**:
```bash
# macOS/Linux
export CLASSPATH="lib/mysql-connector-java-8.x.jar:."

# Windows
set CLASSPATH=lib\mysql-connector-java-8.x.jar;.
```

3. **Compile all Java files**:
```bash
# macOS/Linux
javac -d bin src/db/*.java src/model/*.java src/dao/*.java src/datastructure/*.java src/ui/*.java src/Main.java

# Windows
javac -d bin src\db\*.java src\model\*.java src\dao\*.java src\datastructure\*.java src\ui\*.java src\Main.java
```

4. **Run the application**:
```bash
# macOS/Linux
java -cp "bin:lib/mysql-connector-java-8.x.jar" Main

# Windows
java -cp "bin;lib\mysql-connector-java-8.x.jar" Main
```

### Using VS Code

1. **Install Extensions**:
   - Install "Extension Pack for Java" by Microsoft
   - Utilities will auto-detect project structure

2. **Configure CLASSPATH** (optional):
   - Open `.vscode/settings.json` and add:
   ```json
   {
     "java.project.referencedLibraries": [
       "lib/mysql-connector-java-8.x.jar"
     ]
   }
   ```

3. **Run**:
   - Click "Run" on Main.java or use `Ctrl+Shift+D`

## Usage Guide

### Dashboard
- View system statistics
- See total patients and appointments
- System status overview

### Add Patient
- Register new patients with complete information
- Validate phone numbers (10 digits)
- View all registered patients
- Search patients by phone number

### Book Appointment
- Find patient by phone number
- Select doctor and specialization
- Choose appointment date and time (30-minute slots)
- Add reason for visit
- View all appointments with status
- Mark appointments as completed or cancelled

## Database Schema

### Patients Table
```sql
- patient_id (INT, AUTO_INCREMENT)
- first_name, last_name (VARCHAR)
- date_of_birth (DATE)
- gender (ENUM: Male, Female, Other)
- blood_group (VARCHAR)
- phone (VARCHAR, UNIQUE)
- email (VARCHAR)
- address, city, state, pincode (VARCHAR)
- registered_on (TIMESTAMP)
```

### Doctors Table
```sql
- doctor_id (INT, AUTO_INCREMENT)
- name (VARCHAR)
- specialization (VARCHAR)
- phone (VARCHAR)
- available_days (VARCHAR)
```

### Appointments Table
```sql
- appointment_id (INT, AUTO_INCREMENT)
- patient_id, doctor_id (INT, FK)
- appointment_date (DATE)
- appointment_time (TIME)
- reason (TEXT)
- status (ENUM: Scheduled, Completed, Cancelled)
- token_number (INT)
- created_at (TIMESTAMP)
```

## Main Classes

### DBConnection (Singleton Pattern)
- Manages database connection
- Ensures single connection instance
- Auto-loads MySQL JDBC driver

### PatientDAO
- Add, retrieve, search, update, delete patients
- Get patient by ID or phone
- Get total patient count

### AppointmentDAO
- Book appointments with auto-generated token
- Retrieve all appointments
- Update appointment status
- Generate token numbers

### PatientQueue
- LinkedList-based queue for walk-in patients
- FIFO operations: enqueue, dequeue, peek
- Display queue and check availability

### PatientLinkedList
- Custom singly linked list implementation
- Add, remove, search patients
- Display all patients
- Reverse list operation

## Color Scheme

- **Primary**: Dark Blue (#003566)
- **Secondary**: Light Blue (#1E508C)
- **Accent**: Red (#D62828)
- **Background**: Light Gray (#F0F0F0)
- **Success**: Green (#34A853)

## Troubleshooting

### Database Connection Error
- Ensure MySQL is running
- Check `db.properties` file exists in project root with correct credentials
- Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`

### JDBC Driver Not Found
- Download latest MySQL Connector JAR
- Place in lib/ directory
- Update CLASSPATH before compilation

### Table Structure Error
- Re-run the SQL script
- Check for duplicate table definitions
- Ensure proper foreign key relationships

## Future Enhancements

- [ ] Doctor management panel
- [ ] Billing and payment tracking
- [ ] Prescription management
- [ ] Medical reports generation
- [ ] Patient history and analytics
- [ ] Staff management
- [ ] SMS/Email notifications

## References

### Languages & Technologies
- Java SE 11 - https://docs.oracle.com/en/java/javase/11/
- Java Swing GUI Toolkit - https://docs.oracle.com/javase/tutorial/uiswing/
- JDBC (Java Database Connectivity) - https://docs.oracle.com/javase/tutorial/jdbc/

### Database
- MySQL 8.0 Documentation - https://dev.mysql.com/doc/refman/8.0/en/
- MySQL Connector/J (JDBC Driver) - https://dev.mysql.com/downloads/connector/j/
- MySQL Workbench - https://www.mysql.com/products/workbench/

### Design Patterns Used
- Singleton Pattern (DBConnection) - https://refactoring.guru/design-patterns/singleton
- DAO Pattern (Data Access Object) - https://www.oracle.com/java/technologies/dataaccessobject.html
- MVC Pattern (Model View Controller) - https://www.oracle.com/technical-resources/articles/javase/mvc.html

### Data Structures
- Linked List - https://www.geeksforgeeks.org/linked-list-data-structure/
- Queue Data Structure - https://www.geeksforgeeks.org/queue-data-structure/

### IDE & Tools
- Apache NetBeans IDE - https://netbeans.apache.org/
- GitHub Version Control - https://github.com/
- MySQL Workbench - https://www.mysql.com/products/workbench/

### Learning Resources
- Java Swing Tutorial - https://www.javatpoint.com/java-swing
- JDBC Tutorial - https://www.javatpoint.com/java-jdbc
- MySQL Tutorial - https://www.w3schools.com/mysql/
- Java OOP Concepts - https://www.geeksforgeeks.org/object-oriented-programming-oops-concept-in-java/
- DAO Design Pattern - https://www.baeldung.com/java-dao-pattern




## License

This project is for educational purposes.

## Support

For issues or questions, check:
1. Database connection settings in DBConnection.java
2. MySQL server status
3. JDBC driver in lib/ folder
4. Correct database schema import

---

**Version**: 1.0.0  
**Last Updated**: April 2026
