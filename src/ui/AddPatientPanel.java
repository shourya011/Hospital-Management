package ui;

import dao.PatientDAO;
import datastructure.PatientQueue;
import datastructure.PatientLinkedList;
import model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

/**
 * AddPatientPanel class for registering new patients
 * Includes form for patient details and table showing all registered patients
 */
public class AddPatientPanel extends JPanel {
    private PatientDAO patientDAO;
    private DataChangeManager dataChangeManager;
    private PatientQueue patientQueue;
    private PatientLinkedList patientLinkedList;
    
    // Form fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dateOfBirthField;
    private JComboBox<String> genderCombo;
    private JComboBox<String> bloodGroupCombo;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField pincodeField;
    
    // Table
    private JTable patientTable;
    private DefaultTableModel tableModel;
    
    // Search
    private JTextField searchPhoneField;
    
    // Modern CK Birla Color Palette (matches MainDashboard)
    private static final Color COLOR_PRIMARY = new Color(192, 39, 45);       // CK Birla red
    private static final Color COLOR_PRIMARY_DARK = new Color(155, 29, 34);  // hover/pressed
    private static final Color COLOR_NAVY = new Color(27, 58, 107);          // headings
    private static final Color COLOR_BG_PAGE = new Color(247, 248, 250);     // page background
    private static final Color COLOR_BG_CARD = Color.WHITE;                  // card background
    private static final Color COLOR_TEXT_BODY = new Color(74, 74, 74);      // general text
    private static final Color COLOR_BORDER = new Color(229, 231, 235);      // borders
    private static final Color COLOR_TEXT_MUTED = new Color(150, 150, 160);  // secondary labels
    
    // Fonts (matching MainDashboard)
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    
    /**
     * Constructor initializing the panel
     */
    public AddPatientPanel(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
        this.dataChangeManager = DataChangeManager.getInstance();
        this.patientQueue = new PatientQueue();
        this.patientLinkedList = new PatientLinkedList();
        
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
        
        // Load existing patients
        loadPatients();
    }
    
    /**
     * Create the title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(COLOR_NAVY);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 24, 15, 24));
        
        JLabel titleLabel = new JLabel("Register New Patient");
        titleLabel.setFont(FONT_HEADING);
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    /**
     * Create the patient registration form panel
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(COLOR_BG_CARD);
        formPanel.setPreferredSize(new Dimension(400, 0));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(24, 20, 24, 20)
        ));
        
        // First Name and Last Name
        JPanel namePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        namePanel.setOpaque(false);
        namePanel.add(createLabeledField("First Name", ""));
        firstNameField = (JTextField) ((JPanel) namePanel.getComponent(0)).getComponent(1);
        
        namePanel.add(createLabeledField("Last Name", ""));
        lastNameField = (JTextField) ((JPanel) namePanel.getComponent(1)).getComponent(1);
        
        formPanel.add(createFieldLabel("Name Information"));
        formPanel.add(namePanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Date of Birth
        JPanel dobPanel = createLabeledField("Date of Birth (dd/MM/yyyy)", "");
        dateOfBirthField = (JTextField) dobPanel.getComponent(1);
        formPanel.add(createFieldLabel("Birth Information"));
        formPanel.add(dobPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Gender and Blood Group
        JPanel genderPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        genderPanel.setOpaque(false);
        
        JPanel genderLabelPanel = new JPanel(new BorderLayout());
        genderLabelPanel.setOpaque(false);
        JLabel genderLabel = new JLabel("Gender");
        genderLabel.setFont(FONT_SMALL);
        genderLabel.setForeground(COLOR_TEXT_MUTED);
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(FONT_BODY);
        genderCombo.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        genderLabelPanel.add(genderLabel, BorderLayout.NORTH);
        genderLabelPanel.add(genderCombo, BorderLayout.CENTER);
        genderPanel.add(genderLabelPanel);
        
        JPanel bloodPanel = new JPanel(new BorderLayout());
        bloodPanel.setOpaque(false);
        JLabel bloodLabel = new JLabel("Blood Group");
        bloodLabel.setFont(FONT_SMALL);
        bloodLabel.setForeground(COLOR_TEXT_MUTED);
        bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        bloodGroupCombo.setFont(FONT_BODY);
        bloodGroupCombo.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        bloodPanel.add(bloodLabel, BorderLayout.NORTH);
        bloodPanel.add(bloodGroupCombo, BorderLayout.CENTER);
        genderPanel.add(bloodPanel);
        
        formPanel.add(createFieldLabel("Medical Information"));
        formPanel.add(genderPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Phone and Email
        JPanel contactPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        contactPanel.setOpaque(false);
        contactPanel.add(createLabeledField("Phone", ""));
        phoneField = (JTextField) ((JPanel) contactPanel.getComponent(0)).getComponent(1);
        contactPanel.add(createLabeledField("Email", ""));
        emailField = (JTextField) ((JPanel) contactPanel.getComponent(1)).getComponent(1);
        
        formPanel.add(createFieldLabel("Contact Information"));
        formPanel.add(contactPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // Address
        JPanel addressLabelPanel = new JPanel(new BorderLayout());
        addressLabelPanel.setOpaque(false);
        JLabel addressLabel = new JLabel("Address");
        addressLabel.setFont(FONT_SMALL);
        addressLabel.setForeground(COLOR_TEXT_MUTED);
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(FONT_BODY);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressLabelPanel.add(addressLabel, BorderLayout.NORTH);
        addressLabelPanel.add(addressScroll, BorderLayout.CENTER);
        formPanel.add(addressLabelPanel);
        formPanel.add(Box.createVerticalStrut(10));
        
        // City, State, Pincode
        JPanel locationPanel = new JPanel(new GridLayout(1, 3, 8, 0));
        locationPanel.setOpaque(false);
        locationPanel.add(createLabeledField("City", ""));
        cityField = (JTextField) ((JPanel) locationPanel.getComponent(0)).getComponent(1);
        locationPanel.add(createLabeledField("State", ""));
        stateField = (JTextField) ((JPanel) locationPanel.getComponent(1)).getComponent(1);
        locationPanel.add(createLabeledField("Pincode", ""));
        pincodeField = (JTextField) ((JPanel) locationPanel.getComponent(2)).getComponent(1);
        
        formPanel.add(createFieldLabel("Address Information"));
        formPanel.add(locationPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton registerBtn = new JButton("REGISTER PATIENT");
        registerBtn.setFont(FONT_SUBHEAD);
        registerBtn.setBackground(COLOR_PRIMARY);
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setPreferredSize(new Dimension(200, 40));
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(COLOR_PRIMARY_DARK);
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(COLOR_PRIMARY);
            }
        });
        registerBtn.addActionListener(e -> registerPatient());
        buttonPanel.add(registerBtn);
        
        JButton clearBtn = new JButton("CLEAR FORM");
        clearBtn.setFont(FONT_SUBHEAD);
        clearBtn.setBackground(COLOR_TEXT_MUTED);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setOpaque(true);
        clearBtn.setPreferredSize(new Dimension(200, 40));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearForm());
        buttonPanel.add(clearBtn);
        
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalGlue());
        
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBackground(COLOR_BG_PAGE);
        formScroll.setBorder(null);
        
        JPanel scrollWrapper = new JPanel(new BorderLayout());
        scrollWrapper.setBackground(COLOR_BG_PAGE);
        scrollWrapper.add(formScroll, BorderLayout.CENTER);
        return scrollWrapper;
    }
    
    /**
     * Create a field label
     */
    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SMALL);
        label.setForeground(COLOR_NAVY);
        return label;
    }
    
    /**
     * Create a labeled field
     */
    private JPanel createLabeledField(String labelText, String initialValue) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_SMALL);
        label.setForeground(COLOR_TEXT_MUTED);
        
        JTextField field = new JTextField(initialValue);
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        field.setBackground(Color.WHITE);
        
        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    /**
     * Create the table panel showing all patients
     */
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(COLOR_BG_CARD);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(COLOR_BG_CARD);
        
        JLabel searchLabel = new JLabel("Search by Phone:");
        searchLabel.setFont(FONT_BODY);
        searchLabel.setForeground(COLOR_TEXT_BODY);
        
        searchPhoneField = new JTextField();
        searchPhoneField.setFont(FONT_BODY);
        searchPhoneField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        searchPhoneField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchPatients();
            }
        });
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(FONT_SMALL);
        searchBtn.setBackground(COLOR_PRIMARY);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBtn.addActionListener(e -> searchPatients());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(FONT_SMALL);
        refreshBtn.setBackground(COLOR_NAVY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setOpaque(true);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadPatients());
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchPhoneField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);
        searchPanel.add(refreshBtn, BorderLayout.AFTER_LINE_ENDS);
        
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "Phone", "Gender", "Blood Group", "Registered On"};
        tableModel = new DefaultTableModel(new Object[0][0], columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        patientTable = new JTable(tableModel);
        patientTable.setFont(FONT_BODY);
        patientTable.setRowHeight(36);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setGridColor(COLOR_BORDER);
        patientTable.setBackground(COLOR_BG_CARD);
        patientTable.setSelectionBackground(new Color(192, 39, 45, 30));
        patientTable.setSelectionForeground(COLOR_TEXT_BODY);
        patientTable.getTableHeader().setBackground(COLOR_NAVY);
        patientTable.getTableHeader().setForeground(Color.WHITE);
        patientTable.getTableHeader().setFont(FONT_SUBHEAD);
        
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBackground(COLOR_BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    /**
     * Load all patients into the table
     */
    private void loadPatients() {
        tableModel.setRowCount(0);
        ArrayList<Patient> patients = patientDAO.getAllPatients();
        
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{
                p.getPatientId(),
                p.getFullName(),
                p.getPhone(),
                p.getGender(),
                p.getBloodGroup(),
                p.getRegisteredOn()
            });
        }
    }
    
    /**
     * Search patients by phone
     */
    private void searchPatients() {
        String searchPhone = searchPhoneField.getText().trim();
        
        if (searchPhone.isEmpty()) {
            loadPatients();
            return;
        }
        
        tableModel.setRowCount(0);
        ArrayList<Patient> patients = patientDAO.getAllPatients();
        
        for (Patient p : patients) {
            if (p.getPhone().contains(searchPhone)) {
                tableModel.addRow(new Object[]{
                    p.getPatientId(),
                    p.getFullName(),
                    p.getPhone(),
                    p.getGender(),
                    p.getBloodGroup(),
                    p.getRegisteredOn()
                });
            }
        }
    }
    
    /**
     * Register a new patient
     */
    private void registerPatient() {
        // Validate inputs
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create patient object
            Patient patient = new Patient(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                LocalDate.parse(dateOfBirthField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                (String) genderCombo.getSelectedItem(),
                (String) bloodGroupCombo.getSelectedItem(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressArea.getText().trim(),
                cityField.getText().trim(),
                stateField.getText().trim(),
                pincodeField.getText().trim()
            );
            
            // Add to database
            if (patientDAO.addPatient(patient)) {
                // Add to data structures
                Patient savedPatient = patientDAO.searchPatientByPhone(patient.getPhone());
                if (savedPatient != null) {
                    patientQueue.enqueue(savedPatient);
                    patientLinkedList.add(savedPatient);
                }
                
                JOptionPane.showMessageDialog(this,
                    "Patient registered successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                loadPatients();
                // Notify all listeners that patient data has changed
                dataChangeManager.notifyPatientDataChanged();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to register patient. Please try again.",
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
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Last name is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (dateOfBirthField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Date of birth is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone number is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (phone.length() != 10 || !phone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone number must be 10 digits", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check if phone already exists
        Patient existing = patientDAO.searchPatientByPhone(phone);
        if (existing != null) {
            JOptionPane.showMessageDialog(this, "Phone number already registered", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (addressArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address is required", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    /**
     * Clear all form fields
     */
    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        dateOfBirthField.setText("");
        genderCombo.setSelectedIndex(0);
        bloodGroupCombo.setSelectedIndex(0);
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        cityField.setText("");
        stateField.setText("");
        pincodeField.setText("");
        searchPhoneField.setText("");
    }
}
