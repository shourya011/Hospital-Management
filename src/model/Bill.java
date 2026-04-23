package model;

import java.time.LocalDateTime;

/**
 * Bill model class representing a billing record in the hospital system
 */
public class Bill {
    private int billId;
    private int patientId;
    private int appointmentId;          // 0 if not linked to an appointment
    private String patientName;         // populated via JOIN in DAO
    private String billNumber;          // format: RBH-BILL-YYYYMMDD-XXXX
    private double consultationFee;
    private double medicineCharges;
    private double roomCharges;
    private double labCharges;
    private double otherCharges;
    private double discount;            // absolute discount amount
    private double tax;                 // absolute tax amount (e.g. 5% GST)
    private double totalAmount;         // grand total after discount + tax
    private String paymentStatus;       // 'Pending', 'Paid', 'Partially Paid'
    private String paymentMode;         // 'Cash', 'Card', 'UPI', 'Insurance'
    private String billDate;            // stored as string from DB (DATETIME)
    private String notes;

    /**
     * Default constructor
     */
    public Bill() {
    }

    /**
     * Constructor with all database fields (used when reading from DB)
     */
    public Bill(int billId, int patientId, int appointmentId, String patientName,
                String billNumber, double consultationFee, double medicineCharges,
                double roomCharges, double labCharges, double otherCharges,
                double discount, double tax, double totalAmount,
                String paymentStatus, String paymentMode, String billDate, String notes) {
        this.billId = billId;
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.billNumber = billNumber;
        this.consultationFee = consultationFee;
        this.medicineCharges = medicineCharges;
        this.roomCharges = roomCharges;
        this.labCharges = labCharges;
        this.otherCharges = otherCharges;
        this.discount = discount;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMode = paymentMode;
        this.billDate = billDate;
        this.notes = notes;
    }

    /**
     * Constructor without billId and billNumber (for new bill creation)
     */
    public Bill(int patientId, int appointmentId,
                double consultationFee, double medicineCharges,
                double roomCharges, double labCharges, double otherCharges,
                double discount, double tax, double totalAmount,
                String paymentStatus, String paymentMode, String notes) {
        this.patientId = patientId;
        this.appointmentId = appointmentId;
        this.consultationFee = consultationFee;
        this.medicineCharges = medicineCharges;
        this.roomCharges = roomCharges;
        this.labCharges = labCharges;
        this.otherCharges = otherCharges;
        this.discount = discount;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
        this.paymentMode = paymentMode;
        this.notes = notes;
    }

    // ─── Getters and Setters ───────────────────────────────────────────────────

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public double getMedicineCharges() {
        return medicineCharges;
    }

    public void setMedicineCharges(double medicineCharges) {
        this.medicineCharges = medicineCharges;
    }

    public double getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(double roomCharges) {
        this.roomCharges = roomCharges;
    }

    public double getLabCharges() {
        return labCharges;
    }

    public void setLabCharges(double labCharges) {
        this.labCharges = labCharges;
    }

    public double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Calculate subtotal (sum of all charge components, before discount and tax)
     */
    public double getSubtotal() {
        return consultationFee + medicineCharges + roomCharges + labCharges + otherCharges;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId=" + billId +
                ", billNumber='" + billNumber + '\'' +
                ", patientId=" + patientId +
                ", patientName='" + patientName + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", billDate='" + billDate + '\'' +
                '}';
    }
}
