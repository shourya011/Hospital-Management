package ui;

import java.util.ArrayList;
import java.util.List;

/**
 * DataChangeManager is a singleton that manages data change notifications across panels
 * Allows panels to register as listeners and broadcasts change events to all registered listeners
 */
public class DataChangeManager {
    private static DataChangeManager instance;
    private List<DataChangeListener> listeners;
    
    /**
     * Private constructor to prevent instantiation
     */
    private DataChangeManager() {
        listeners = new ArrayList<>();
    }
    
    /**
     * Get the singleton instance of DataChangeManager
     * @return The single instance of DataChangeManager
     */
    public static synchronized DataChangeManager getInstance() {
        if (instance == null) {
            instance = new DataChangeManager();
        }
        return instance;
    }
    
    /**
     * Register a listener for data changes
     * @param listener The listener to register
     */
    public void addListener(DataChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Unregister a listener for data changes
     * @param listener The listener to unregister
     */
    public void removeListener(DataChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that doctor data has changed
     */
    public void notifyDoctorDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onDoctorDataChanged();
        }
    }
    
    /**
     * Notify all listeners that patient data has changed
     */
    public void notifyPatientDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onPatientDataChanged();
        }
    }
    
    /**
     * Notify all listeners that appointment data has changed
     */
    public void notifyAppointmentDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onAppointmentDataChanged();
        }
    }

    /**
     * Notify all listeners that bill data has changed
     */
    public void notifyBillDataChanged() {
        for (DataChangeListener listener : listeners) {
            listener.onBillDataChanged();
        }
    }
}
