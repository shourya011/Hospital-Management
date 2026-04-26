package ui;

import dao.AppointmentDAO;
import dao.PatientDAO;
import model.Appointment;
import model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * AppointmentPanel class for booking and managing appointments
 * Includes form for appointment details and table showing all appointments
 */
public class AppointmentPanel extends JPanel implements DataChangeListener {
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DataChangeManager dataChangeManager;
    
    // Form fields
    private JTextField patientPhoneField;
    private JTextField patientNameField;
    private JComboBox<String> doctorCombo;
    private JTextField appointmentDateField;
    private JComboBox<String> appointmentTimeCombo;
    private JTextArea reasonArea;
    
    // Table
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    
    // Modern CK Birla Color Palette (matches MainDashboard)
    private static final Color COLOR_PRIMARY = new Color(192, 39, 45);       // CK Birla red
    private static final Color COLOR_PRIMARY_DARK = new Color(155, 29, 34);  // hover/pressed
    private static final Color COLOR_NAVY = new Color(27, 58, 107);          // headings
    private static final Color COLOR_BG_PAGE = new Color(247, 248, 250);     // page background
    private static final Color COLOR_BG_CARD = Color.WHITE;                  // card background
    private static final Color COLOR_TEXT_BODY = new Color(74, 74, 74);      // general text
    private static final Color COLOR_BORDER = new Color(229, 231, 235);      // borders
    private static final Color COLOR_SUCCESS = new Color(29, 158, 117);      // success/active
    private static final Color COLOR_TEXT_MUTED = new Color(150, 150, 160);  // secondary labels
    
    // Fonts (matching MainDashboard)
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    
    // Backward compatibility aliases
    private static final Color DARK_BLUE = COLOR_NAVY;
    private static final Color LIGHT_GRAY = COLOR_BG_PAGE;
    private static final Color RED_ACCENT = COLOR_PRIMARY;
    private static final Color WHITE = Color.WHITE;
    private static final Color GREEN = COLOR_SUCCESS;
    
    private int selectedPatientId = -1;
    
    /**
     * Constructor initializing the panel
     */
    public AppointmentPanel(AppointmentDAO appointmentDAO, PatientDAO patientDAO) {
        this.appointmentDAO = appointmentDAO;
        this.patientDAO = patientDAO;
        this.dataChangeManager = DataChangeManager.getInstance();
        
        // Register this panel as a listener for data changes
        dataChangeManager.addListener(this);
        
        setLayout(new BorderLayout());
        setBackground(COLOR_BG_PAGE);
        
        // Title panel
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main content
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBackground(COLOR_BG_PAGE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));
        
        mainContent.add(createFormPanel(), BorderLayout.WEST);
        mainContent.add(createTablePanel(), BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        
        // Load appointments
        loadAppointments();
    }
    
    /**
     * Create the title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(COLOR_NAVY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 24, 15, 24));
        
        JLabel titleLabel = new JLabel("Book Appointment");
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    /**
     * Create the appointment booking form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(COLOR_BG_CARD);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(24, 20, 24, 20)
        ));
        formPanel.setPreferredSize(new Dimension(400, 0));
        
        // Patient Phone and Find button
        formPanel.add(createFieldLabel("Patient Information"));
        
        JPanel phonePanel = new JPanel(new BorderLayout(10, 0));
        phonePanel.setOpaque(false);
        
        JLabel phoneLabel = new JLabel("Patient Phone");
        phoneLabel.setFont(FONT_SMALL);
        phoneLabel.setForeground(COLOR_TEXT_MUTED);
        
        patientPhoneField = new JTextField();
        patientPhoneField.setFont(FONT_BODY);
        patientPhoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JButton findBtn = new JButton("FIND PATIENT");
        findBtn.setFont(FONT_SMALL);
        findBtn.setBackground(COLOR_NAVY);
        findBtn.setForeground(Color.WHITE);
        findBtn.setFocusPainted(false);
        findBtn.setBorderPainted(false);
        findBtn.setOpaque(true);
        findBtn.setPreferredSize(new Dimension(120, 32));
        findBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        findBtn.addActionListener(e -> findPatient());
        
        JPanel phoneLabelPanel = new JPanel(new BorderLayout());
        phoneLabelPanel.setOpaque(false);
        phoneLabelPanel.add(phoneLabel, BorderLayout.NORTH);
        phoneLabelPanel.add(patientPhoneField, BorderLayout.CENTER);
        
        phonePanel.add(phoneLabelPanel, BorderLayout.CENTER);
        phonePanel.add(findBtn, BorderLayout.EAST);
        
        formPanel.add(phonePanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Patient Name (read-only)
        JPanel namePanel = createLabeledField("Patient Name", "");
        patientNameField = (JTextField) namePanel.getComponent(1);
        patientNameField.setEditable(false);
        patientNameField.setBackground(new Color(230, 230, 230));
        formPanel.add(namePanel);
        formPanel.add(Box.createVerticalStrut(15));
        
        // Doctor Selection
        formPanel.add(createFieldLabel("Doctor & Appointment Details"));
        
        JPanel doctorLabelPanel = new JPanel(new BorderLayout());
        doctorLabelPanel.setOpaque(false);
        JLabel doctorLabel = new JLabel("Select Doctor");
        doctorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        doctorLabel.setForeground(new Color(80, 80, 80));
        doctorCombo = new JComboBox<>();
        doctorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loadDoctors();
        doctorLabelPanel.add(doctorLabel, BorderLayout.NORTH);
        doctorLabelPanel.add(doctorCombo, BorderLayout.CENTER);
        
        formPanel.add(doctorLabelPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Appointment Date
        JPanel datePanel = createLabeledField("Appointment Date (dd/MM/yyyy)", "");
        appointmentDateField = (JTextField) datePanel.getComponent(1);
        formPanel.add(datePanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Appointment Time
        JPanel timeLabelPanel = new JPanel(new BorderLayout());
        timeLabelPanel.setOpaque(false);
        JLabel timeLabel = new JLabel("Appointment Time");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(new Color(80, 80, 80));
        appointmentTimeCombo = new JComboBox<>();
        appointmentTimeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loadTimeSlots();
        timeLabelPanel.add(timeLabel, BorderLayout.NORTH);
        timeLabelPanel.add(appointmentTimeCombo, BorderLayout.CENTER);
        
        formPanel.add(timeLabelPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Reason
        JPanel reasonLabelPanel = new JPanel(new BorderLayout());
        reasonLabelPanel.setOpaque(false);
        JLabel reasonLabel = new JLabel("Reason for Visit");
        reasonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        reasonLabel.setForeground(new Color(80, 80, 80));
        reasonArea = new JTextArea(3, 20);
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonLabelPanel.add(reasonLabel, BorderLayout.NORTH);
        reasonLabelPanel.add(reasonScroll, BorderLayout.CENTER);
        formPanel.add(reasonLabelPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton bookBtn = new JButton("BOOK APPOINTMENT");
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        bookBtn.setBackground(RED_ACCENT);
        bookBtn.setForeground(WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setBorderPainted(false);
        bookBtn.setOpaque(true);
        bookBtn.setPreferredSize(new Dimension(200, 38));
        bookBtn.addActionListener(e -> bookAppointment());
        buttonPanel.add(bookBtn);
        
        JButton clearBtn = new JButton("CLEAR");
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        clearBtn.setBackground(new Color(160, 160, 160));
        clearBtn.setForeground(WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setPreferredSize(new Dimension(200, 38));
        clearBtn.addActionListener(e -> clearForm());
        buttonPanel.add(clearBtn);
        
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalGlue());
        
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBackground(LIGHT_GRAY);
        formScroll.setBorder(null);
        
        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(LIGHT_GRAY);
        scrollWrapper.add(formScroll, BorderLayout.CENTER);
        return scrollWrapper;
    }
    
    /**
     * Create a field label
     */
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(DARK_BLUE);
        return label;
    }
    
    /**
     * Create a labeled field
     */
    private JPanel createLabeledField(String labelText, String initialValue) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        label.setForeground(new Color(80, 80, 80));
        
        JTextField field = new JTextField(initialValue);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    /**
     * Create the table panel showing all appointments
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Title label
        JLabel tableTitle = new JLabel("All Appointments");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableTitle.setForeground(DARK_BLUE);
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Token", "Patient Name", "Doctor", "Specialization", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(new Object[0][0], columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        appointmentTable.setRowHeight(25);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getTableHeader().setBackground(DARK_BLUE);
        appointmentTable.getTableHeader().setForeground(WHITE);
        appointmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        // Add right-click context menu
        appointmentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    int row = appointmentTable.rowAtPoint(evt.getPoint());
                    if (row >= 0) {
                        appointmentTable.setRowSelectionInterval(row, row);
                        showContextMenu(evt.getComponent(), evt.getX(), evt.getY(), row);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBackground(WHITE);
        scrollPane.setBorder(null);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * Load all doctors into combo box
     */
    private void loadDoctors() {
        doctorCombo.removeAllItems();
        ArrayList<String> doctors = appointmentDAO.getAllDoctors();
        for (String doctor : doctors) {
            String[] parts = doctor.split(":::");
            doctorCombo.addItem(parts[1]);
        }
    }
    
    /**
     * Load time slots into combo box
     */
    private void loadTimeSlots() {
        appointmentTimeCombo.removeAllItems();
        String[] timeSlots = {
            "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
            "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
            "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        };
        
        for (String slot : timeSlots) {
            appointmentTimeCombo.addItem(slot);
        }
    }
    
    /**
     * Load all appointments into the table
     */
    private void loadAppointments() {
        tableModel.setRowCount(0);
        ArrayList<Appointment> appointments = appointmentDAO.getAllAppointments();
        
        for (Appointment apt : appointments) {
            tableModel.addRow(new Object[]{
                apt.getTokenNumber(),
                apt.getPatientName(),
                apt.getDoctorName(),
                apt.getSpecialization(),
                apt.getAppointmentDate(),
                apt.getAppointmentTime(),
                apt.getStatus()
            });
        }
    }
    
    /**
     * Find patient by phone number
     */
    private void findPatient() {
        String phone = patientPhoneField.getText().trim();
        
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter patient phone number", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Patient patient = patientDAO.searchPatientByPhone(phone);
        
        if (patient != null) {
            patientNameField.setText(patient.getFullName());
            selectedPatientId = patient.getPatientId();
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found", "Error", JOptionPane.ERROR_MESSAGE);
            patientNameField.setText("");
            selectedPatientId = -1;
        }
    }
    
    /**
     * Book an appointment
     */
    private void bookAppointment() {
        // Validate inputs
        if (!validateForm()) {
            return;
        }
        
        try {
            // Get doctor ID from selected doctor
            String selectedDoctor = (String) doctorCombo.getSelectedItem();
            ArrayList<String> allDoctors = appointmentDAO.getAllDoctors();
            int doctorId = -1;
            
            for (String doctor : allDoctors) {
                String[] parts = doctor.split(":::");
                if (parts[1].equals(selectedDoctor)) {
                    doctorId = Integer.parseInt(parts[0]);
                    break;
                }
            }
            
            // Create appointment object
            LocalDate appointmentDate = LocalDate.parse(appointmentDateField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime appointmentTime = convertTimeStringToLocalTime((String) appointmentTimeCombo.getSelectedItem());
            
            Appointment appointment = new Appointment(
                selectedPatientId,
                doctorId,
                appointmentDate,
                appointmentTime,
                reasonArea.getText().trim()
            );
            
            // Book appointment
            if (appointmentDAO.bookAppointment(appointment)) {
                JOptionPane.showMessageDialog(this,
                    "Appointment Booked Successfully!\nToken Number: " + appointment.getTokenNumber(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                loadAppointments();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to book appointment. Please try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Use dd/MM/yyyy",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "An error occurred: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Validate form inputs
     */
    private boolean validateForm() {
        if (selectedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Please find a patient first", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (doctorCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (appointmentDateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter appointment date", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (reasonArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter reason for visit", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Convert time string to LocalTime
     */
    private LocalTime convertTimeStringToLocalTime(String timeString) {
        String[] parts = timeString.split(" ");
        String time = parts[0];
        String period = parts[1];
        
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);
        
        if (period.equals("PM") && hour != 12) {
            hour += 12;
        } else if (period.equals("AM") && hour == 12) {
            hour = 0;
        }
        
        return LocalTime.of(hour, minute);
    }
    
    /**
     * Show context menu for appointments
     */
    private void showContextMenu(Component component, int x, int y, int row) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem completeItem = new JMenuItem("Mark Completed");
        completeItem.addActionListener(e -> markCompleted(row));
        menu.add(completeItem);
        
        JMenuItem cancelItem = new JMenuItem("Cancel Appointment");
        cancelItem.addActionListener(e -> cancelAppointment(row));
        menu.add(cancelItem);
        
        menu.show(component, x, y);
    }
    
    /**
     * Mark appointment as completed
     */
    private void markCompleted(int row) {
        int token = (int) tableModel.getValueAt(row, 0);
        ArrayList<Appointment> appointments = appointmentDAO.getAllAppointments();
        
        for (Appointment apt : appointments) {
            if (apt.getTokenNumber() == token) {
                if (appointmentDAO.completeAppointment(apt.getAppointmentId())) {
                    JOptionPane.showMessageDialog(this, "Appointment marked as completed", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAppointments();
                }
                break;
            }
        }
    }
    
    /**
     * Cancel an appointment
     */
    private void cancelAppointment(int row) {
        int token = (int) tableModel.getValueAt(row, 0);
        ArrayList<Appointment> appointments = appointmentDAO.getAllAppointments();
        
        for (Appointment apt : appointments) {
            if (apt.getTokenNumber() == token) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to cancel this appointment?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (appointmentDAO.cancelAppointment(apt.getAppointmentId())) {
                        JOptionPane.showMessageDialog(this, "Appointment cancelled", "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadAppointments();
                    }
                }
                break;
            }
        }
    }
    
    /**
     * Clear all form fields
     */
    private void clearForm() {
        patientPhoneField.setText("");
        patientNameField.setText("");
        appointmentDateField.setText("");
        reasonArea.setText("");
        selectedPatientId = -1;
    }
    
    /**
     * Called when doctor data changes (doctor added/updated/deleted)
     * Refresh the doctor dropdown list
     */
    @Override
    public void onDoctorDataChanged() {
        loadDoctors();
    }
    
    /**
     * Called when patient data changes (patient added/updated/deleted)
     * Refresh the patient data if needed
     */
    @Override
    public void onPatientDataChanged() {
        // Patient data changed, no immediate action needed in appointment panel
        // unless a patient was deleted (which would require validation)
    }
    
    /**
     * Called when appointment data changes (appointment added/updated/deleted)
     * Refresh the appointments table
     */
    @Override
    public void onAppointmentDataChanged() {
        loadAppointments();
    }

    /**
     * Called when bill data changes — no action needed in this panel
     */
    @Override
    public void onBillDataChanged() {
        // No action needed in appointment panel
    }
}
