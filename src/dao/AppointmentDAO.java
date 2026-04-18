package dao;

import db.DBConnection;
import model.Appointment;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * AppointmentDAO class handles all database operations related to appointments
 * Uses JDBC and PreparedStatements for secure database access
 */
public class AppointmentDAO {
    private Connection connection;
    
    /**
     * Constructor initializing the database connection
     */
    public AppointmentDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }
    
    /**
     * Book an appointment and generate token number
     * @param appointment Appointment object to be booked
     * @return true if booking was successful, false otherwise
     */
    public boolean bookAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, " +
                     "appointment_time, reason, status, token_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            // Generate token number (based on current time and random)
            int tokenNumber = generateTokenNumber();
            
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setInt(2, appointment.getDoctorId());
            pstmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, "Scheduled");
            pstmt.setInt(7, tokenNumber);
            
            int result = pstmt.executeUpdate();
            appointment.setTokenNumber(tokenNumber);
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not book appointment!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get all appointments from the database
     * @return ArrayList of all appointments with patient and doctor details
     */
    public ArrayList<Appointment> getAllAppointments() {
        ArrayList<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name, p.last_name, d.doctor_name, d.specialization " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "ORDER BY a.token_number ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment apt = new Appointment(
                    rs.getInt("appointment_id"),
                    rs.getInt("patient_id"),
                    rs.getInt("doctor_id"),
                    rs.getDate("appointment_date").toLocalDate(),
                    rs.getTime("appointment_time").toLocalTime(),
                    rs.getString("reason"),
                    rs.getString("status"),
                    rs.getInt("token_number"),
                    rs.getTimestamp("created_at").toString()
                );
                apt.setPatientName(rs.getString("first_name") + " " + rs.getString("last_name"));
                apt.setDoctorName(rs.getString("doctor_name"));
                apt.setSpecialization(rs.getString("specialization"));
                appointments.add(apt);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch appointments!");
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Get appointments for a specific patient
     * @param patientId Patient ID
     * @return ArrayList of patient's appointments
     */
    public ArrayList<Appointment> getAppointmentsByPatient(int patientId) {
        ArrayList<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.first_name, p.last_name, d.doctor_name, d.specialization " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.patient_id = ? " +
                     "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment apt = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getInt("token_number"),
                        rs.getTimestamp("created_at").toString()
                    );
                    apt.setPatientName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    apt.setDoctorName(rs.getString("doctor_name"));
                    apt.setSpecialization(rs.getString("specialization"));
                    appointments.add(apt);
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch patient appointments!");
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    /**
     * Update appointment status
     * @param appointmentId Appointment ID
     * @param newStatus New status (Scheduled, Completed, Cancelled)
     * @return true if update was successful, false otherwise
     */
    public boolean updateStatus(int appointmentId, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, appointmentId);
            
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not update appointment status!");
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get appointment by ID
     * @param appointmentId Appointment ID
     * @return Appointment object if found, null otherwise
     */
    public Appointment getAppointmentById(int appointmentId) {
        String sql = "SELECT a.*, p.first_name, p.last_name, d.doctor_name, d.specialization " +
                     "FROM appointments a " +
                     "JOIN patients p ON a.patient_id = p.patient_id " +
                     "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                     "WHERE a.appointment_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Appointment apt = new Appointment(
                        rs.getInt("appointment_id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date").toLocalDate(),
                        rs.getTime("appointment_time").toLocalTime(),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getInt("token_number"),
                        rs.getTimestamp("created_at").toString()
                    );
                    apt.setPatientName(rs.getString("first_name") + " " + rs.getString("last_name"));
                    apt.setDoctorName(rs.getString("doctor_name"));
                    apt.setSpecialization(rs.getString("specialization"));
                    return apt;
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch appointment!");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all doctors
     * @return ArrayList of doctors with their specializations
     */
    public ArrayList<String> getAllDoctors() {
        ArrayList<String> doctors = new ArrayList<>();
        String sql = "SELECT doctor_id, doctor_name, specialization FROM doctors ORDER BY specialization, doctor_name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String doctorInfo = rs.getInt("doctor_id") + ":::" +
                                   rs.getString("doctor_name") + " - " +
                                   rs.getString("specialization");
                doctors.add(doctorInfo);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch doctors!");
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    /**
     * Cancel an appointment
     * @param appointmentId Appointment ID to cancel
     * @return true if cancellation was successful, false otherwise
     */
    public boolean cancelAppointment(int appointmentId) {
        return updateStatus(appointmentId, "Cancelled");
    }
    
    /**
     * Mark an appointment as completed
     * @param appointmentId Appointment ID
     * @return true if update was successful, false otherwise
     */
    public boolean completeAppointment(int appointmentId) {
        return updateStatus(appointmentId, "Completed");
    }
    
    /**
     * Generate a unique token number for appointment
     * @return unique token number
     */
    private int generateTokenNumber() {
        String sql = "SELECT MAX(token_number) as max_token FROM appointments";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int maxToken = rs.getInt("max_token");
                if (maxToken == 0) {
                    return 1001;
                }
                return maxToken + 1;
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not generate token number!");
            e.printStackTrace();
        }
        
        return 1001;
    }
    
    /**
     * Get total appointments count
     * @return total number of appointments
     */
    public int getTotalAppointments() {
        String sql = "SELECT COUNT(*) as count FROM appointments WHERE status = 'Scheduled'";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not get appointment count!");
            e.printStackTrace();
        }
        
        return 0;
    }
}
