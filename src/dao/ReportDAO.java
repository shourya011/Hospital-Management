package dao;

import db.DBConnection;
import datastructure.ReportDataList;
import model.ReportRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class ReportDAO {

    public ArrayList<ReportRow> getAppointmentSummary(Date from, Date to) {
        ReportDataList<ReportRow> list = new ReportDataList<>();
        String sql = "SELECT status, COUNT(*) as count FROM appointments WHERE appointment_date >= ? AND appointment_date <= ? GROUP BY status";
        
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ReportRow(rs.getString("status"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.getAll();
    }

    public ArrayList<ReportRow> getDoctorWiseAppointments(Date from, Date to) {
        ReportDataList<ReportRow> list = new ReportDataList<>();
        String sql = "SELECT d.doctor_name, COUNT(a.appointment_id) as count FROM doctors d " +
                     "LEFT JOIN appointments a ON d.doctor_id = a.doctor_id " +
                     "AND a.appointment_date >= ? AND a.appointment_date <= ? " +
                     "GROUP BY d.doctor_id, d.doctor_name";
                     
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ReportRow(rs.getString("doctor_name"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        ArrayList<ReportRow> result = list.getAll();
        
        // Manual insertion sort descending as requested
        for (int i = 1; i < result.size(); i++) {
            ReportRow key = result.get(i);
            int j = i - 1;
            while (j >= 0 && result.get(j).getValue() < key.getValue()) {
                result.set(j + 1, result.get(j));
                j = j - 1;
            }
            result.set(j + 1, key);
        }
        
        return result;
    }

    public ArrayList<ReportRow> getSpecializationWiseAppointments(Date from, Date to) {
        ReportDataList<ReportRow> list = new ReportDataList<>();
        String sql = "SELECT d.specialization, COUNT(a.appointment_id) as count FROM doctors d " +
                     "JOIN appointments a ON d.doctor_id = a.doctor_id " +
                     "WHERE a.appointment_date >= ? AND a.appointment_date <= ? " +
                     "GROUP BY d.specialization";
                     
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ReportRow(rs.getString("specialization"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.getAll();
    }

    public ArrayList<ReportRow> getPatientGenderDistribution() {
        ReportDataList<ReportRow> list = new ReportDataList<>();
        String sql = "SELECT gender, COUNT(*) as count FROM patients GROUP BY gender";
        
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ReportRow(rs.getString("gender"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.getAll();
    }

    public ArrayList<ReportRow> getMonthlyRegistrations(int year) {
        ReportDataList<ReportRow> list = new ReportDataList<>();
        String sql = "SELECT MONTH(registered_on) as month, COUNT(*) as count FROM patients WHERE YEAR(registered_on) = ? GROUP BY MONTH(registered_on)";
        
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new ReportRow("Month " + rs.getInt("month"), rs.getInt("count")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list.getAll();
    }

    public ArrayList<ReportRow> getTotalSummaryStats(Date from, Date to) {
        ReportDataList<ReportRow> list = new ReportDataList<>();
        Connection conn = DBConnection.getInstance().getConnection();
        
        // Query 1: Total Patients ever
        try {
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as count FROM patients")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) list.add(new ReportRow("Total Patients (All Time)", rs.getInt("count")));
            }
            
            // Query 2: New Patients in range
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as count FROM patients WHERE registered_on >= ? AND registered_on <= ?")) {
                stmt.setDate(1, new java.sql.Date(from.getTime()));
                // Add 1 day to 'to' date to make sure we include all times in that day (up to 23:59:59)
                stmt.setDate(2, new java.sql.Date(to.getTime() + 86400000L));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) list.add(new ReportRow("New Patients (Range)", rs.getInt("count")));
            }
            
            // Query 3: Total Appointments in range
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments WHERE appointment_date >= ? AND appointment_date <= ?")) {
                stmt.setDate(1, new java.sql.Date(from.getTime()));
                stmt.setDate(2, new java.sql.Date(to.getTime()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) list.add(new ReportRow("Total Appointments (Range)", rs.getInt("count")));
            }
            
            // Query 4: Completed in range
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) as count FROM appointments WHERE status = 'Completed' AND appointment_date >= ? AND appointment_date <= ?")) {
                stmt.setDate(1, new java.sql.Date(from.getTime()));
                stmt.setDate(2, new java.sql.Date(to.getTime()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) list.add(new ReportRow("Completed Appointments", rs.getInt("count")));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list.getAll();
    }
}