package dao;

import db.DBConnection;
import model.Doctor;

import java.sql.*;
import java.util.ArrayList;

/**
 * DoctorDAO class handles all database operations related to doctors
 * Uses JDBC and PreparedStatements for secure database access
 */
public class DoctorDAO {
    private Connection connection;
    
    /**
     * Constructor initializing the database connection
     */
    public DoctorDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }
    
    /**
     * Add a new doctor to the database
     * @param doctor Doctor object to be added
     * @return true if insertion was successful, false otherwise
     */
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (doctor_name, specialization, phone_number, email, " +
                     "gender, experience_years, availability, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getDoctorName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getPhoneNumber());
            pstmt.setString(4, doctor.getEmail());
            pstmt.setString(5, doctor.getGender());
            pstmt.setInt(6, doctor.getExperienceYears());
            pstmt.setString(7, doctor.getAvailability());
            pstmt.setString(8, doctor.getStatus());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not add doctor!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all doctors from the database
     * @return ArrayList of all doctors
     */
    public ArrayList<Doctor> getAllDoctors() {
        ArrayList<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY doctor_id ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Doctor doctor = new Doctor(
                    rs.getInt("doctor_id"),
                    rs.getString("doctor_name"),
                    rs.getString("specialization"),
                    rs.getString("phone_number"),
                    rs.getString("email"),
                    rs.getString("gender"),
                    rs.getInt("experience_years"),
                    rs.getString("availability"),
                    rs.getString("status"),
                    rs.getTimestamp("registered_on").toString()
                );
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch doctors!");
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Search for a doctor by phone number
     * @param phoneNumber Doctor phone number
     * @return Doctor object if found, null otherwise
     */
    public Doctor searchDoctorByPhone(String phoneNumber) {
        String sql = "SELECT * FROM doctors WHERE phone_number = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phoneNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getInt("experience_years"),
                        rs.getString("availability"),
                        rs.getString("status"),
                        rs.getTimestamp("registered_on").toString()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not search doctor!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Search for a doctor by ID
     * @param doctorId Doctor ID
     * @return Doctor object if found, null otherwise
     */
    public Doctor getDoctorById(int doctorId) {
        String sql = "SELECT * FROM doctors WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getInt("experience_years"),
                        rs.getString("availability"),
                        rs.getString("status"),
                        rs.getTimestamp("registered_on").toString()
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch doctor!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Search for doctors by specialization
     * @param specialization Doctor specialization
     * @return ArrayList of doctors with matching specialization
     */
    public ArrayList<Doctor> searchDoctorBySpecialization(String specialization) {
        ArrayList<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ? ORDER BY doctor_id ASC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, specialization);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = new Doctor(
                        rs.getInt("doctor_id"),
                        rs.getString("doctor_name"),
                        rs.getString("specialization"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("gender"),
                        rs.getInt("experience_years"),
                        rs.getString("availability"),
                        rs.getString("status"),
                        rs.getTimestamp("registered_on").toString()
                    );
                    doctors.add(doctor);
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not search doctors by specialization!");
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Delete a doctor from the database
     * @param doctorId Doctor ID to be deleted
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, doctorId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not delete doctor!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update doctor information
     * @param doctor Doctor object with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET doctor_name = ?, specialization = ?, phone_number = ?, " +
                     "email = ?, gender = ?, experience_years = ?, availability = ?, status = ? " +
                     "WHERE doctor_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getDoctorName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getPhoneNumber());
            pstmt.setString(4, doctor.getEmail());
            pstmt.setString(5, doctor.getGender());
            pstmt.setInt(6, doctor.getExperienceYears());
            pstmt.setString(7, doctor.getAvailability());
            pstmt.setString(8, doctor.getStatus());
            pstmt.setInt(9, doctor.getDoctorId());
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not update doctor!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get count of total doctors
     * @return total number of doctors
     */
    public int getTotalDoctors() {
        String sql = "SELECT COUNT(*) as count FROM doctors";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not get doctor count!");
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Get all unique specializations
     * @return ArrayList of specializations
     */
    public ArrayList<String> getAllSpecializations() {
        ArrayList<String> specializations = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM doctors ORDER BY specialization ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                specializations.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch specializations!");
            e.printStackTrace();
        }
        
        return specializations;
    }
}
