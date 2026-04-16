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
    
    // Color scheme
    private static final Color DARK_BLUE = new Color(0, 53, 102);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color RED_ACCENT = new Color(214, 40, 40);
    private static final Color WHITE = Color.WHITE;
    
    /**
     * Constructor initializing the panel
     */
    public AddPatientPanel(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
        this.patientQueue = new PatientQueue();
        this.patientLinkedList = new PatientLinkedList();
        
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
        
        // Load existing patients
        loadPatients();
    }
    
    /**
     * Create the title panel
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(DARK_BLUE);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("Register New Patient");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    /**
     * Create the patient registration form panel
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
        genderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        genderLabel.setForeground(new Color(80, 80, 80));
        genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        genderLabelPanel.add(genderLabel, BorderLayout.NORTH);
        genderLabelPanel.add(genderCombo, BorderLayout.CENTER);
        genderPanel.add(genderLabelPanel);
        
        JPanel bloodPanel = new JPanel(new BorderLayout());
        bloodPanel.setOpaque(false);
        JLabel bloodLabel = new JLabel("Blood Group");
        bloodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        bloodLabel.setForeground(new Color(80, 80, 80));
        bloodGroupCombo = new JComboBox<>(new String[]{"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"});
        bloodGroupCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        addressLabel.setForeground(new Color(80, 80, 80));
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
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
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        registerBtn.setBackground(RED_ACCENT);
        registerBtn.setForeground(WHITE);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setPreferredSize(new Dimension(200, 38));
        registerBtn.addActionListener(e -> registerPatient());
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
     * Create the table panel showing all patients
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
                searchPatients();
            }
        });
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        searchBtn.setBackground(RED_ACCENT);
        searchBtn.setForeground(WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.addActionListener(e -> searchPatients());
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        refreshBtn.setBackground(DARK_BLUE);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setOpaque(true);
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
        patientTable.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        patientTable.setRowHeight(25);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getTableHeader().setBackground(DARK_BLUE);
        patientTable.getTableHeader().setForeground(WHITE);
        patientTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBackground(WHITE);
        scrollPane.setBorder(null);
        
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
