package dao;

import db.DBConnection;
import model.Bill;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * BillDAO class handles all database operations related to bills
 * Uses JDBC and PreparedStatements for secure database access
 */
public class BillDAO {
    private Connection connection;

    /**
     * Constructor initializing the database connection
     */
    public BillDAO() {
        this.connection = DBConnection.getInstance().getConnection();
    }

    // ─── Bill Number Generation ────────────────────────────────────────────────

    /**
     * Generate a unique bill number in the format RBH-BILL-YYYYMMDD-XXXX
     * @return formatted bill number string
     */
    public String generateBillNumber() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = "SELECT COUNT(*) as count FROM bills WHERE DATE(bill_date) = CURDATE()";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int seq = rs.getInt("count") + 1;
                return String.format("RBH-BILL-%s-%04d", dateStr, seq);
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not generate bill number!");
            e.printStackTrace();
        }
        return "RBH-BILL-" + dateStr + "-0001";
    }

    // ─── Create ───────────────────────────────────────────────────────────────

    /**
     * Create a new bill in the database
     * @param bill Bill object to be inserted
     * @return true if insertion was successful, false otherwise
     */
    public boolean createBill(Bill bill) {
        String billNumber = generateBillNumber();
        String sql = "INSERT INTO bills (patient_id, appointment_id, bill_number, " +
                     "consultation_fee, medicine_charges, room_charges, lab_charges, other_charges, " +
                     "discount, tax, total_amount, payment_status, payment_mode, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, bill.getPatientId());
            if (bill.getAppointmentId() > 0) {
                pstmt.setInt(2, bill.getAppointmentId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setString(3, billNumber);
            pstmt.setDouble(4, bill.getConsultationFee());
            pstmt.setDouble(5, bill.getMedicineCharges());
            pstmt.setDouble(6, bill.getRoomCharges());
            pstmt.setDouble(7, bill.getLabCharges());
            pstmt.setDouble(8, bill.getOtherCharges());
            pstmt.setDouble(9, bill.getDiscount());
            pstmt.setDouble(10, bill.getTax());
            pstmt.setDouble(11, bill.getTotalAmount());
            pstmt.setString(12, bill.getPaymentStatus());
            pstmt.setString(13, bill.getPaymentMode());
            pstmt.setString(14, bill.getNotes());

            int result = pstmt.executeUpdate();
            if (result > 0) {
                // Retrieve generated bill_id and bill_number back into the object
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        bill.setBillId(generatedKeys.getInt(1));
                    }
                }
                bill.setBillNumber(billNumber);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not create bill!");
            e.printStackTrace();
            return false;
        }
    }

    // ─── Read ─────────────────────────────────────────────────────────────────

    /**
     * Get all bills from the database, joined with patient name
     * @return ArrayList of all bills
     */
    public ArrayList<Bill> getAllBills() {
        ArrayList<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.*, CONCAT(p.first_name, ' ', p.last_name) AS patient_name " +
                     "FROM bills b " +
                     "JOIN patients p ON b.patient_id = p.patient_id " +
                     "ORDER BY b.bill_id DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                bills.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch bills!");
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get a single bill by its ID
     * @param billId Bill primary key
     * @return Bill object if found, null otherwise
     */
    public Bill getBillById(int billId) {
        String sql = "SELECT b.*, CONCAT(p.first_name, ' ', p.last_name) AS patient_name " +
                     "FROM bills b " +
                     "JOIN patients p ON b.patient_id = p.patient_id " +
                     "WHERE b.bill_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBill(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch bill by ID!");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all bills for a specific patient
     * @param patientId Patient primary key
     * @return ArrayList of bills belonging to the patient
     */
    public ArrayList<Bill> getBillsByPatientId(int patientId) {
        ArrayList<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.*, CONCAT(p.first_name, ' ', p.last_name) AS patient_name " +
                     "FROM bills b " +
                     "JOIN patients p ON b.patient_id = p.patient_id " +
                     "WHERE b.patient_id = ? " +
                     "ORDER BY b.bill_id DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapResultSetToBill(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch bills by patient ID!");
            e.printStackTrace();
        }
        return bills;
    }

    /**
     * Get all bills filtered by payment status
     * @param status 'Pending', 'Paid', or 'Partially Paid'
     * @return ArrayList of matching bills
     */
    public ArrayList<Bill> getBillsByStatus(String status) {
        ArrayList<Bill> bills = new ArrayList<>();
        String sql = "SELECT b.*, CONCAT(p.first_name, ' ', p.last_name) AS patient_name " +
                     "FROM bills b " +
                     "JOIN patients p ON b.patient_id = p.patient_id " +
                     "WHERE b.payment_status = ? " +
                     "ORDER BY b.bill_id DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapResultSetToBill(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not fetch bills by status!");
            e.printStackTrace();
        }
        return bills;
    }

    // ─── Update ───────────────────────────────────────────────────────────────

    /**
     * Update an existing bill's payment status
     * @param billId Bill primary key
     * @param status New payment status
     * @return true if update was successful, false otherwise
     */
    public boolean updatePaymentStatus(int billId, String status) {
        String sql = "UPDATE bills SET payment_status = ? WHERE bill_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, billId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not update payment status!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing bill record
     * @param bill Bill object with updated information
     * @return true if update was successful, false otherwise
     */
    public boolean updateBill(Bill bill) {
        String sql = "UPDATE bills SET patient_id = ?, appointment_id = ?, " +
                     "consultation_fee = ?, medicine_charges = ?, room_charges = ?, " +
                     "lab_charges = ?, other_charges = ?, discount = ?, tax = ?, " +
                     "total_amount = ?, payment_status = ?, payment_mode = ?, notes = ? " +
                     "WHERE bill_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bill.getPatientId());
            if (bill.getAppointmentId() > 0) {
                pstmt.setInt(2, bill.getAppointmentId());
            } else {
                pstmt.setNull(2, Types.INTEGER);
            }
            pstmt.setDouble(3, bill.getConsultationFee());
            pstmt.setDouble(4, bill.getMedicineCharges());
            pstmt.setDouble(5, bill.getRoomCharges());
            pstmt.setDouble(6, bill.getLabCharges());
            pstmt.setDouble(7, bill.getOtherCharges());
            pstmt.setDouble(8, bill.getDiscount());
            pstmt.setDouble(9, bill.getTax());
            pstmt.setDouble(10, bill.getTotalAmount());
            pstmt.setString(11, bill.getPaymentStatus());
            pstmt.setString(12, bill.getPaymentMode());
            pstmt.setString(13, bill.getNotes());
            pstmt.setInt(14, bill.getBillId());

            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not update bill!");
            e.printStackTrace();
            return false;
        }
    }

    // ─── Delete ───────────────────────────────────────────────────────────────

    /**
     * Delete a bill from the database
     * @param billId Bill primary key to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteBill(int billId) {
        String sql = "DELETE FROM bills WHERE bill_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, billId);
            int result = pstmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            System.err.println("ERROR: Could not delete bill!");
            e.printStackTrace();
            return false;
        }
    }

    // ─── Aggregate Queries ────────────────────────────────────────────────────

    /**
     * Get total revenue from all paid bills
     * @return total revenue as double
     */
    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as revenue FROM bills WHERE payment_status = 'Paid'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not calculate total revenue!");
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Get count of pending payment bills
     * @return count of pending bills
     */
    public int getPendingBillsCount() {
        String sql = "SELECT COUNT(*) as count FROM bills WHERE payment_status = 'Pending' OR payment_status = 'Partially Paid'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not count pending bills!");
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get today's collection amount
     * @return total collections for today
     */
    public double getTodayCollections() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as today FROM bills " +
                     "WHERE DATE(bill_date) = CURDATE() AND payment_status = 'Paid'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("today");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not calculate today's collections!");
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Get revenue breakdown by payment mode
     * @return ArrayList of String arrays: [mode, count, sum]
     */
    public ArrayList<String[]> getRevenueByPaymentMode() {
        ArrayList<String[]> result = new ArrayList<>();
        String sql = "SELECT payment_mode, COUNT(*) as count, COALESCE(SUM(total_amount), 0) as total " +
                     "FROM bills WHERE payment_status = 'Paid' GROUP BY payment_mode ORDER BY total DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("payment_mode"),
                    String.valueOf(rs.getInt("count")),
                    String.format("%.2f", rs.getDouble("total"))
                });
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not get revenue by payment mode!");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get count of all bills
     * @return total number of bills
     */
    public int getTotalBillsCount() {
        String sql = "SELECT COUNT(*) as count FROM bills";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not get bills count!");
            e.printStackTrace();
        }
        return 0;
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    /**
     * Map a ResultSet row to a Bill object
     * @param rs ResultSet positioned at the current row
     * @return populated Bill object
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setPatientId(rs.getInt("patient_id"));
        bill.setAppointmentId(rs.getInt("appointment_id"));  // returns 0 if NULL
        bill.setPatientName(rs.getString("patient_name"));
        bill.setBillNumber(rs.getString("bill_number"));
        bill.setConsultationFee(rs.getDouble("consultation_fee"));
        bill.setMedicineCharges(rs.getDouble("medicine_charges"));
        bill.setRoomCharges(rs.getDouble("room_charges"));
        bill.setLabCharges(rs.getDouble("lab_charges"));
        bill.setOtherCharges(rs.getDouble("other_charges"));
        bill.setDiscount(rs.getDouble("discount"));
        bill.setTax(rs.getDouble("tax"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setPaymentStatus(rs.getString("payment_status"));
        bill.setPaymentMode(rs.getString("payment_mode"));
        Timestamp ts = rs.getTimestamp("bill_date");
        bill.setBillDate(ts != null ? ts.toString() : "");
        bill.setNotes(rs.getString("notes"));
        return bill;
    }
}
