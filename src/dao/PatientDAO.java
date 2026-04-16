package dao;

import db.DBConnection;
import model.Patient;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * PatientDAO class handles all database operations related to patients
 * Uses JDBC and PreparedStatements for secure database access
 */
public class PatientDAO {
    private Connection connection;
    
    /**
     * Constructor initializing the database connection
     */
    public PatientDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }
    
    /**
     * Add a new patient to the database
     * @param patient Patient object to be added
     * @return true if insertion was successful, false otherwise
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (first_name, last_name, date_of_birth, gender, blood_group, " +
                     "phone, email, address, city, state, pincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getBloodGroup());
            pstmt.setString(6, patient.getPhone());
            pstmt.setString(7, patient.getEmail());
            pstmt.setString(8, patient.getAddress());
            pstmt.setString(9, patient.getCity());
            pstmt.setString(10, patient.getState());
            pstmt.setString(11, patient.getPincode());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not add patient!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all patients from the database
     * @return ArrayList of all patients
     */
    public ArrayList<Patient> getAllPatients() {
        ArrayList<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY patient_id ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Patient patient = new Patient(
                    rs.getInt("patient_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getDate("date_of_birth").toLocalDate(),
                    rs.getString("gender"),
                    rs.getString("blood_group"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("pincode"),
                    rs.getTimestamp("registered_on").toString()
                );
                patients.add(patient);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch patients!");
            e.printStackTrace();
        }
        
        return patients;
    }
    
    /**
     * Search for a patient by phone number
     * @param phone Patient phone number
     * @return Patient object if found, null otherwise
     */
    public Patient searchPatientByPhone(String phone) {
        String sql = "SELECT * FROM patients WHERE phone = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("gender"),
                        rs.getString("blood_group"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("pincode"),
                        rs.getTimestamp("registered_on").toString()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not search patient!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Search for a patient by ID
     * @param patientId Patient ID
     * @return Patient object if found, null otherwise
     */
    public Patient getPatientById(int patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                        rs.getInt("patient_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDate("date_of_birth").toLocalDate(),
                        rs.getString("gender"),
                        rs.getString("blood_group"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("city"),
                        rs.getString("state"),
                        rs.getString("pincode"),
                        rs.getTimestamp("registered_on").toString()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch patient!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Delete a patient from the database
     * @param patientId Patient ID to be deleted
     * @return true if deletion was successful, false otherwise
     */
    public boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not delete patient!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update patient information
     * @param patient Patient object with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                     "gender = ?, blood_group = ?, phone = ?, email = ?, address = ?, " +
                     "city = ?, state = ?, pincode = ? WHERE patient_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getBloodGroup());
            pstmt.setString(6, patient.getPhone());
            pstmt.setString(7, patient.getEmail());
            pstmt.setString(8, patient.getAddress());
            pstmt.setString(9, patient.getCity());
            pstmt.setString(10, patient.getState());
            pstmt.setString(11, patient.getPincode());
            pstmt.setInt(12, patient.getPatientId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not update patient!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get count of total patients
     * @return total number of patients
     */
    public int getTotalPatients() {
        String sql = "SELECT COUNT(*) as count FROM patients";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not get patient count!");
            e.printStackTrace();
        }
        
        return 0;
    }
}
