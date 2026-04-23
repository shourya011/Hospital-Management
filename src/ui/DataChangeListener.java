package ui;

/**
 * DataChangeListener interface for observing data changes in panels
 * Panels can implement this to listen for changes in doctors, patients, appointments, and bills
 */
public interface DataChangeListener {
    
    /**
     * Called when doctors are added, updated, or deleted
     */
    void onDoctorDataChanged();
    
    /**
     * Called when patients are added, updated, or deleted
     */
    void onPatientDataChanged();
    
    /**
     * Called when appointments are added, updated, or deleted
     */
    void onAppointmentDataChanged();

    /**
     * Called when bills are added, updated, or deleted
     */
    void onBillDataChanged();
}
