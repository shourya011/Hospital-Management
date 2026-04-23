# Report & Analytics Module

## Overview
The **Report & Analytics Module** provides comprehensive, data-driven insights into the operational metrics of the RBH Hospital Management System. It allows hospital administrators to track appointments, monitor doctor performance, analyze patient demographics, and export data for external reporting.

## Key Features
*   **Dynamic Data Filtering**: Filter reports dynamically using "From" and "To" date spinners.
*   **Appointment Summary**: Displays the total count of appointments categorized by status (Scheduled, Completed, Cancelled).
*   **Doctor-wise Appointments**: Ranks doctors based on the number of appointments handled, sorted in descending order.
*   **Specialization-wise Appointments**: Groups appointments to show which medical specialties are in highest demand.
*   **Patient Demographics**: Shows the distribution of patients across different genders.
*   **Monthly Patient Registrations**: Tracks patient acquisition trends on a month-by-month basis for a given year.
*   **Total Summary Stats**: Provides an at-a-glance dashboard view of overall system metrics (Total Patients, New Patients in range, Total Appointments, Completed Appointments).
*   **CSV Export**: One-click export of any generated report to a `.csv` file for use in spreadsheet software like Excel.
*   **Live Min/Max Metrics**: Automatically calculates and highlights the "Highest" and "Lowest" records for the currently active dataset.

## Technical Implementation

### 1. Architecture (MVC Pattern)
*   **Model (`model/ReportRow.java`)**: A lightweight container class storing a `String label` and `int value` to represent a single row of report data.
*   **DAO (`dao/ReportDAO.java`)**: Handles all secure database communications using `PreparedStatement` to prevent SQL injection. Efficiently groups and aggregates data on the database side before sending it to the application.
*   **View (`ui/ReportPanel.java`)**: A highly interactive `JPanel` integrated into the main `CardLayout`. Features a sleek table, interactive date spinners, and dynamically updating summary cards.

### 2. Custom Data Structures
To fulfill strict academic and project constraints, this module deliberately avoids Java's built-in Collections framework for list management where possible:
*   **`datastructure/ReportDataList.java`**: A custom-implemented Generic Singly Linked List. It utilizes an internal `Node<T>` class to collect rows from the database ResultSet before converting them into an array format suitable for table rendering.

### 3. Custom Algorithms
*   **Insertion Sort**: Instead of using `Collections.sort()`, the module includes a manual implementation of the **Insertion Sort** algorithm within `ReportDAO.getDoctorWiseAppointments()`. This algorithm sorts the retrieved doctors in descending order based on their appointment counts.

### 4. Database Integration
*   The module integrates seamlessly with the existing `DBConnection` Singleton class. 
*   Connection handling was heavily optimized to avoid `ConnectionIsClosedException` issues by ensuring the global singleton connection is not prematurely closed by automated try-with-resources blocks during sequential reporting queries.

## Usage Instructions
1. Navigate to the **Reports** tab from the main dashboard sidebar.
2. Select the desired report type from the dropdown menu.
3. If applicable, adjust the **From Date** and **To Date** spinners.
4. Click **Generate Report** to update the table and bottom summary cards.
5. (Optional) Click **Export CSV** to save the current table view to your local file system.