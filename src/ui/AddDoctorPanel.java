package ui;

import dao.DoctorDAO;
import model.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * AddDoctorPanel class for registering new doctors
 * Includes form for doctor details and table showing all registered doctors
 */
public class AddDoctorPanel extends JPanel {
    private DoctorDAO doctorDAO;
    private DataChangeManager dataChangeManager;
    
    // Form fields
    private JTextField doctorNameField;
    private JComboBox<String> specializationCombo;
    private JTextField phoneNumberField;
    private JTextField emailField;
    private JComboBox<String> genderCombo;
    private JSpinner experienceYearsSpinner;
    private JTextArea availabilityArea;
    private JComboBox<String> statusCombo;
    
    // Table
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    
    // Search
    private JTextField searchPhoneField;
    
    // Edit mode tracking
    private int editingDoctorId = -1;
    
    // Color scheme
    private static final Color DARK_BLUE = new Color(0, 53, 102);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color RED_ACCENT = new Color(214, 40, 40);
    private static final Color WHITE = Color.WHITE;
    
    /**
     * Constructor initializing the panel
     */
    public AddDoctorPanel(DoctorDAO doctorDAO) {
        this.doctorDAO = doctorDAO;
        this.dataChangeManager = DataChangeManager.getInstance();
        
        setLayout(new BorderLayout());
        setBackground(LIGHT_GRAY);
        
        // Title panel
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main content
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBackground(LIGHT_GRAY);
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        mainContent.add(createFormPanel(), BorderLayout.WEST);
        mainContent.add(createTablePanel(), BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        
        // Load existing doctors
        loadDoctors();
    }
    
    /**
     * Create the title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(DARK_BLUE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Register New Doctor");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    /**
     * Create the doctor registration form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(WHITE);
        formPanel.setPreferredSize(new Dimension(400, 0));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Doctor Name
        JPanel namePanel = createLabeledField("Doctor Name", "");
        doctorNameField = (JTextField) namePanel.getComponent(1);
        formPanel.add(createFieldLabel("Basic Information"));
        formPanel.add(namePanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Specialization and Gender
        JPanel specGenderPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        specGenderPanel.setOpaque(false);
        
        JPanel specLabelPanel = new JPanel(new BorderLayout());
        specLabelPanel.setOpaque(false);
        JLabel specLabel = new JLabel("Specialization");
        specLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        specLabel.setForeground(new Color(80, 80, 80));
        specializationCombo = new JComboBox<>(new String[]{
            "Cardiology", "Neurology", "Pediatrics", "Orthopedics", 
            "General Medicine", "Dermatology", "Psychiatry", "Oncology",
            "Gynecology", "Urology", "ENT", "Ophthalmology"
        });
        specializationCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        specLabelPanel.add(specLabel, BorderLayout.NORTH);
        specLabelPanel.add(specializationCombo, BorderLayout.CENTER);
        specGenderPanel.add(specLabelPanel);
        
        JPanel genderLabelPanel = new JPanel(new BorderLayout());
        genderLabelPanel.setOpaque(false);
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        genderLabel.setForeground(new Color(80, 80, 80));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        genderLabelPanel.add(genderLabel, BorderLayout.NORTH);
        genderLabelPanel.add(genderCombo, BorderLayout.CENTER);
        specGenderPanel.add(genderLabelPanel);
        
        formPanel.add(createFieldLabel("Professional Information"));
        formPanel.add(specGenderPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Phone and Email
        JPanel contactPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contactPanel.setOpaque(false);
        contactPanel.add(createLabeledField("Phone Number", ""));
        phoneNumberField = (JTextField) ((JPanel) contactPanel.getComponent(0)).getComponent(1);
        contactPanel.add(createLabeledField("Email", ""));
        emailField = (JTextField) ((JPanel) contactPanel.getComponent(1)).getComponent(1);
        
        formPanel.add(createFieldLabel("Contact Information"));
        formPanel.add(contactPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Experience Years
        JPanel experiencePanel = new JPanel(new BorderLayout());
        experiencePanel.setOpaque(false);
        JLabel expLabel = new JLabel("Experience (Years)");
        expLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        expLabel.setForeground(new Color(80, 80, 80));
        experienceYearsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
        experienceYearsSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JPanel expLabelPanel = new JPanel(new BorderLayout());
        expLabelPanel.setOpaque(false);
        expLabelPanel.add(expLabel, BorderLayout.NORTH);
        expLabelPanel.add(experienceYearsSpinner, BorderLayout.CENTER);
        experiencePanel.add(expLabelPanel, BorderLayout.CENTER);
        
        formPanel.add(createFieldLabel("Work Experience"));
        formPanel.add(experiencePanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Availability
        JPanel availabilityLabelPanel = new JPanel(new BorderLayout());
        availabilityLabelPanel.setOpaque(false);
        JLabel availabilityLabel = new JLabel("Availability (e.g., Mon-Fri 9AM-5PM)");
        availabilityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        availabilityLabel.setForeground(new Color(80, 80, 80));
        availabilityArea = new JTextArea(2, 30);
        availabilityArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        availabilityArea.setLineWrap(true);
        availabilityArea.setWrapStyleWord(true);
        JScrollPane availabilityScroll = new JScrollPane(availabilityArea);
        availabilityLabelPanel.add(availabilityLabel, BorderLayout.NORTH);
        availabilityLabelPanel.add(availabilityScroll, BorderLayout.CENTER);
        formPanel.add(availabilityLabelPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Status
        JPanel statusLabelPanel = new JPanel(new BorderLayout());
        statusLabelPanel.setOpaque(false);
        JLabel statusLabel = new JLabel("Status");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(new Color(80, 80, 80));
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabelPanel.add(statusLabel, BorderLayout.NORTH);
        statusLabelPanel.add(statusCombo, BorderLayout.CENTER);
        formPanel.add(statusLabelPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton registerBtn = new JButton("REGISTER DOCTOR");
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        registerBtn.setBackground(RED_ACCENT);
        registerBtn.setForeground(WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setPreferredSize(new Dimension(200, 38));
        registerBtn.addActionListener(e -> {
            if (editingDoctorId == -1) {
                registerDoctor();
            } else {
                updateDoctor();
            }
        });
        buttonPanel.add(registerBtn);
        
        JButton clearBtn = new JButton("CLEAR FORM");
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
     * Create the table panel showing all doctors
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(WHITE);
        
        JLabel searchLabel = new JLabel("Search by Phone:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        searchPhoneField = new JTextField();
        searchPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        searchPhoneField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchDoctors();
            }
        });
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        searchBtn.setBackground(RED_ACCENT);
        searchBtn.setForeground(WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.addActionListener(e -> searchDoctors());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        refreshBtn.setBackground(DARK_BLUE);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setOpaque(true);
        refreshBtn.addActionListener(e -> loadDoctors());
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchPhoneField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        searchPanel.add(refreshBtn, BorderLayout.AFTER_LINE_ENDS);
        
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email", "Gender", "Experience", "Status"};
        tableModel = new DefaultTableModel(new Object[0][0], columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        doctorTable = new JTable(tableModel);
        doctorTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        doctorTable.setRowHeight(25);
        doctorTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        doctorTable.getTableHeader().setBackground(DARK_BLUE);
        doctorTable.getTableHeader().setForeground(WHITE);
        doctorTable.setSelectionBackground(new Color(200, 220, 240));
        
        JScrollPane tableScroll = new JScrollPane(doctorTable);
        tableScroll.setPreferredSize(new Dimension(600, 300));
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setBackground(WHITE);
        
        JButton editBtn = new JButton("EDIT");
        editBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        editBtn.setBackground(DARK_BLUE);
        editBtn.setForeground(WHITE);
        editBtn.setFocusPainted(false);
        editBtn.setBorderPainted(false);
        editBtn.setOpaque(true);
        editBtn.setPreferredSize(new Dimension(100, 30));
        editBtn.addActionListener(e -> editSelectedDoctor());
        actionPanel.add(editBtn);
        
        JButton deleteBtn = new JButton("DELETE");
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        deleteBtn.setBackground(RED_ACCENT);
        deleteBtn.setForeground(WHITE);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setOpaque(true);
        deleteBtn.setPreferredSize(new Dimension(100, 30));
        deleteBtn.addActionListener(e -> deleteSelectedDoctor());
        actionPanel.add(deleteBtn);
        
        tablePanel.add(actionPanel, BorderLayout.SOUTH);
        
        return tablePanel;
    }
    
    /**
     * Load all doctors into the table
     */
    private void loadDoctors() {
        tableModel.setRowCount(0);
        ArrayList<Doctor> doctors = doctorDAO.getAllDoctors();
        
        for (Doctor doctor : doctors) {
            tableModel.addRow(new Object[]{
                doctor.getDoctorId(),
                doctor.getDoctorName(),
                doctor.getSpecialization(),
                doctor.getPhoneNumber(),
                doctor.getEmail(),
                doctor.getGender(),
                doctor.getExperienceYears(),
                doctor.getStatus()
            });
        }
    }
    
    /**
     * Search doctors by phone number (partial match)
     */
    private void searchDoctors() {
        String phone = searchPhoneField.getText().trim();
        
        if (phone.isEmpty()) {
            loadDoctors();
            return;
        }
        
        tableModel.setRowCount(0);
        ArrayList<Doctor> allDoctors = doctorDAO.getAllDoctors();
        ArrayList<Doctor> results = new ArrayList<>();
        
        // Search with partial match (contains)
        for (Doctor doctor : allDoctors) {
            if (doctor.getPhoneNumber().contains(phone)) {
                results.add(doctor);
            }
        }
        
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No doctor found with that phone number!",
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
            loadDoctors();
        } else {
            for (Doctor doctor : results) {
                tableModel.addRow(new Object[]{
                    doctor.getDoctorId(),
                    doctor.getDoctorName(),
                    doctor.getSpecialization(),
                    doctor.getPhoneNumber(),
                    doctor.getEmail(),
                    doctor.getGender(),
                    doctor.getExperienceYears(),
                    doctor.getStatus()
                });
            }
        }
    }
    
    /**
     * Register a new doctor
     */
    private void registerDoctor() {
        String doctorName = doctorNameField.getText().trim();
        String specialization = (String) specializationCombo.getSelectedItem();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();
        int experienceYears = (Integer) experienceYearsSpinner.getValue();
        String availability = availabilityArea.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        
        // Validation
        if (doctorName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || availability.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create Doctor object
        Doctor doctor = new Doctor(doctorName, specialization, phoneNumber, email, gender, experienceYears, availability, status);
        
        // Add to database
        if (doctorDAO.addDoctor(doctor)) {
            JOptionPane.showMessageDialog(this, "Doctor registered successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadDoctors();
            // Notify all listeners that doctor data has changed
            dataChangeManager.notifyDoctorDataChanged();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to register doctor. Please try again!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Edit selected doctor
     */
    private void editSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to edit!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        Doctor doctor = doctorDAO.getDoctorById(doctorId);
        
        if (doctor != null) {
            // Set editing mode
            editingDoctorId = doctorId;
            
            // Populate form with doctor data
            doctorNameField.setText(doctor.getDoctorName());
            specializationCombo.setSelectedItem(doctor.getSpecialization());
            phoneNumberField.setText(doctor.getPhoneNumber());
            emailField.setText(doctor.getEmail());
            genderCombo.setSelectedItem(doctor.getGender());
            experienceYearsSpinner.setValue(doctor.getExperienceYears());
            availabilityArea.setText(doctor.getAvailability());
            statusCombo.setSelectedItem(doctor.getStatus());
            
            JOptionPane.showMessageDialog(this, "Edit the fields and click 'REGISTER DOCTOR' button to save changes.",
                    "Edit Mode", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Update an existing doctor
     */
    private void updateDoctor() {
        String doctorName = doctorNameField.getText().trim();
        String specialization = (String) specializationCombo.getSelectedItem();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();
        String gender = (String) genderCombo.getSelectedItem();
        int experienceYears = (Integer) experienceYearsSpinner.getValue();
        String availability = availabilityArea.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        
        // Validation
        if (doctorName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || availability.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Get the doctor and update it
        Doctor doctor = doctorDAO.getDoctorById(editingDoctorId);
        if (doctor != null) {
            doctor.setDoctorName(doctorName);
            doctor.setSpecialization(specialization);
            doctor.setPhoneNumber(phoneNumber);
            doctor.setEmail(email);
            doctor.setGender(gender);
            doctor.setExperienceYears(experienceYears);
            doctor.setAvailability(availability);
            doctor.setStatus(status);
            
            if (doctorDAO.updateDoctor(doctor)) {
                JOptionPane.showMessageDialog(this, "Doctor updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                editingDoctorId = -1;
                clearForm();
                loadDoctors();
                // Notify all listeners that doctor data has changed
                dataChangeManager.notifyDoctorDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update doctor!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Delete selected doctor
     */
    private void deleteSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete!",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int doctorId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String doctorName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + doctorName + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(doctorId)) {
                JOptionPane.showMessageDialog(this, "Doctor deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadDoctors();
                // Notify all listeners that doctor data has changed
                dataChangeManager.notifyDoctorDataChanged();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete doctor!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Clear the form
     */
    private void clearForm() {
        editingDoctorId = -1;
        doctorNameField.setText("");
        specializationCombo.setSelectedIndex(0);
        phoneNumberField.setText("");
        emailField.setText("");
        genderCombo.setSelectedIndex(0);
        experienceYearsSpinner.setValue(0);
        availabilityArea.setText("");
        statusCombo.setSelectedIndex(0);
        searchPhoneField.setText("");
    }
}
