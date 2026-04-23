-- Hospital Management System Database Schema
-- Database: hospital_db
-- Created for RBH (Rukmani Birla Hospital) Management System

-- Create Database
CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- Table: patients
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

-- Table: doctors
CREATE TABLE IF NOT EXISTS doctors (
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

-- Table: appointments
CREATE TABLE IF NOT EXISTS appointments (
    appointment_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    reason TEXT,
    status ENUM('Scheduled', 'Completed', 'Cancelled') DEFAULT 'Scheduled',
    token_number INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE RESTRICT,
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_status (status)
);

-- NOTE: Sample data has been removed to preserve user-added records
-- All existing patient and appointment records will be retained

-- ─────────────────────────────────────────────────────────────────────────────
-- Table: bills
-- Billing Module — added in v1.1
-- ─────────────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS bills (
    bill_id            INT AUTO_INCREMENT PRIMARY KEY,
    patient_id         INT NOT NULL,
    appointment_id     INT DEFAULT NULL,
    bill_number        VARCHAR(30) NOT NULL UNIQUE,
    consultation_fee   DECIMAL(10,2) DEFAULT 0.00,
    medicine_charges   DECIMAL(10,2) DEFAULT 0.00,
    room_charges       DECIMAL(10,2) DEFAULT 0.00,
    lab_charges        DECIMAL(10,2) DEFAULT 0.00,
    other_charges      DECIMAL(10,2) DEFAULT 0.00,
    discount           DECIMAL(10,2) DEFAULT 0.00,
    tax                DECIMAL(10,2) DEFAULT 0.00,
    total_amount       DECIMAL(10,2) DEFAULT 0.00,
    payment_status     ENUM('Pending','Paid','Partially Paid') DEFAULT 'Pending',
    payment_mode       ENUM('Cash','Card','UPI','Insurance') DEFAULT 'Cash',
    bill_date          DATETIME DEFAULT CURRENT_TIMESTAMP,
    notes              TEXT,
    FOREIGN KEY (patient_id)     REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE SET NULL,
    INDEX idx_bill_patient   (patient_id),
    INDEX idx_bill_status    (payment_status),
    INDEX idx_bill_date      (bill_date)
);

-- ─────────────────────────────────────────────────────────────────────────────
-- Seed Data: 5 sample bills
-- Run ONLY on a fresh setup (skip if bills table already has data).
-- patient_id values must match existing rows in your patients table.
-- ─────────────────────────────────────────────────────────────────────────────
INSERT IGNORE INTO bills
    (patient_id, appointment_id, bill_number,
     consultation_fee, medicine_charges, room_charges, lab_charges, other_charges,
     discount, tax, total_amount, payment_status, payment_mode, notes)
VALUES
    (1, NULL, 'RBH-BILL-20260401-0001',
     500.00, 300.00, 0.00, 200.00, 50.00, 0.00, 52.50, 1102.50, 'Paid', 'Cash',
     'Routine check-up'),
    (2, NULL, 'RBH-BILL-20260405-0001',
     750.00, 500.00, 1200.00, 400.00, 100.00, 100.00, 142.50, 2992.50, 'Paid', 'Card',
     'Admitted for observation'),
    (3, NULL, 'RBH-BILL-20260410-0001',
     500.00, 150.00, 0.00, 0.00, 0.00, 0.00, 32.50, 682.50, 'Pending', 'UPI',
     'Awaiting payment'),
    (4, NULL, 'RBH-BILL-20260415-0001',
     1000.00, 800.00, 2400.00, 600.00, 200.00, 200.00, 240.00, 5040.00, 'Paid', 'Insurance',
     'Surgery follow-up'),
    (5, NULL, 'RBH-BILL-20260420-0001',
     500.00, 250.00, 0.00, 350.00, 0.00, 50.00, 52.50, 1102.50, 'Partially Paid', 'Cash',
     'Partial payment received');
