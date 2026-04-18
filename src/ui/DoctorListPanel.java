package ui;

import dao.DoctorDAO;
import model.Doctor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * DoctorListPanel class for viewing and managing doctor list
 * Includes search/filter functionality and edit/delete actions
 */
public class DoctorListPanel extends JPanel implements DataChangeListener {
    private DoctorDAO doctorDAO;
    private DataChangeManager dataChangeManager;
    
    // Table
    private JTable doctorTable;
    private DefaultTableModel tableModel;
    
    // Search
    private JTextField searchPhoneField;
    private JComboBox<String> searchSpecializationCombo;
    
    // Color scheme
    private static final Color DARK_BLUE = new Color(0, 53, 102);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color RED_ACCENT = new Color(214, 40, 40);
    private static final Color WHITE = Color.WHITE;
    
    /**
     * Constructor initializing the panel
     */
    public DoctorListPanel(DoctorDAO doctorDAO) {
        this.doctorDAO = doctorDAO;
        this.dataChangeManager = DataChangeManager.getInstance();
        
        // Register this panel as a listener for data changes
        dataChangeManager.addListener(this);
        
        setLayout(new BorderLayout());
        setBackground(LIGHT_GRAY);
        
        // Title panel
        add(createTitlePanel(), BorderLayout.NORTH);
        
        // Main content
        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBackground(LIGHT_GRAY);
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        mainContent.add(createListPanel(), BorderLayout.CENTER);
        
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
        
        JLabel titleLabel = new JLabel("Doctor List");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        titlePanel.add(titleLabel);
        return titlePanel;
    }
    
    /**
     * Create the doctor list panel with search and filters
     */
    private JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout(0, 10));
        listPanel.setBackground(WHITE);
        listPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Search and filter panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(WHITE);
        
        // Search by phone
        JPanel phoneSearchPanel = new JPanel(new BorderLayout(5, 0));
        phoneSearchPanel.setBackground(WHITE);
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        searchPhoneField = new JTextField();
        searchPhoneField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        searchPhoneField.setPreferredSize(new Dimension(150, 30));
        searchPhoneField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchDoctors();
            }
        });
        phoneSearchPanel.add(phoneLabel, BorderLayout.WEST);
        phoneSearchPanel.add(searchPhoneField, BorderLayout.CENTER);
        
        // Search by specialization
        JPanel specSearchPanel = new JPanel(new BorderLayout(5, 0));
        specSearchPanel.setBackground(WHITE);
        JLabel specLabel = new JLabel("Specialization:");
        specLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        searchSpecializationCombo = new JComboBox<>();
        loadSpecializations();
        searchSpecializationCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        searchSpecializationCombo.addActionListener(e -> searchDoctors());
        specSearchPanel.add(specLabel, BorderLayout.WEST);
        specSearchPanel.add(searchSpecializationCombo, BorderLayout.CENTER);
        
        // Buttons
        JButton searchBtn = new JButton("SEARCH");
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        searchBtn.setBackground(RED_ACCENT);
        searchBtn.setForeground(WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setOpaque(true);
        searchBtn.setPreferredSize(new Dimension(90, 30));
        searchBtn.addActionListener(e -> searchDoctors());
        
        JButton refreshBtn = new JButton("REFRESH");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        refreshBtn.setBackground(DARK_BLUE);
        refreshBtn.setForeground(WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setOpaque(true);
        refreshBtn.setPreferredSize(new Dimension(90, 30));
        refreshBtn.addActionListener(e -> {
            searchPhoneField.setText("");
            searchSpecializationCombo.setSelectedIndex(0);
            loadDoctors();
        });
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(WHITE);
        filterPanel.add(phoneSearchPanel);
        filterPanel.add(specSearchPanel);
        filterPanel.add(searchBtn);
        filterPanel.add(refreshBtn);
        
        searchPanel.add(filterPanel, BorderLayout.WEST);
        
        listPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "Specialization", "Phone", "Email", "Gender", "Experience", "Availability", "Status"};
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
        tableScroll.setPreferredSize(new Dimension(900, 400));
        listPanel.add(tableScroll, BorderLayout.CENTER);
        
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
        
        listPanel.add(actionPanel, BorderLayout.SOUTH);
        
        return listPanel;
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
                doctor.getAvailability(),
                doctor.getStatus()
            });
        }
    }
    
    /**
     * Load all specializations for filter dropdown
     */
    private void loadSpecializations() {
        searchSpecializationCombo.removeAllItems();
        searchSpecializationCombo.addItem("All Specializations");
        ArrayList<String> specializations = doctorDAO.getAllSpecializations();
        for (String spec : specializations) {
            searchSpecializationCombo.addItem(spec);
        }
    }
    
    /**
     * Search doctors by phone and specialization
     */
    private void searchDoctors() {
        String phone = searchPhoneField.getText().trim();
        String specialization = (String) searchSpecializationCombo.getSelectedItem();
        
        tableModel.setRowCount(0);
        ArrayList<Doctor> results = new ArrayList<>();
        
        // Get all doctors
        ArrayList<Doctor> allDoctors = doctorDAO.getAllDoctors();
        
        // Apply filters
        for (Doctor doctor : allDoctors) {
            boolean matchPhone = phone.isEmpty() || doctor.getPhoneNumber().contains(phone);
            boolean matchSpec = specialization.equals("All Specializations") || 
                               doctor.getSpecialization().equals(specialization);
            
            if (matchPhone && matchSpec) {
                results.add(doctor);
            }
        }
        
        // Display results
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No doctors found matching the criteria!",
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
                    doctor.getAvailability(),
                    doctor.getStatus()
                });
            }
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
            // Create edit dialog
            JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                            "Edit Doctor", true);
            editDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            editDialog.setSize(450, 600);
            editDialog.setLocationRelativeTo((Frame) SwingUtilities.getWindowAncestor(this));
            
            JPanel editPanel = new JPanel(new BorderLayout(10, 10));
            editPanel.setBackground(WHITE);
            editPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(WHITE);
            
            // Form fields
            JTextField nameField = new JTextField(doctor.getDoctorName());
            JComboBox<String> specCombo = new JComboBox<>(new String[]{
                "Cardiology", "Neurology", "Pediatrics", "Orthopedics", 
                "General Medicine", "Dermatology", "Psychiatry", "Oncology",
                "Gynecology", "Urology", "ENT", "Ophthalmology"
            });
            specCombo.setSelectedItem(doctor.getSpecialization());
            
            JTextField phoneField = new JTextField(doctor.getPhoneNumber());
            JTextField emailField = new JTextField(doctor.getEmail());
            JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
            genderCombo.setSelectedItem(doctor.getGender());
            
            JSpinner experienceSpinner = new JSpinner(new SpinnerNumberModel(doctor.getExperienceYears(), 0, 50, 1));
            JTextArea availabilityArea = new JTextArea(doctor.getAvailability(), 2, 30);
            availabilityArea.setLineWrap(true);
            availabilityArea.setWrapStyleWord(true);
            
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
            statusCombo.setSelectedItem(doctor.getStatus());
            
            // Add fields to form
            formPanel.add(createFormField("Doctor Name", nameField));
            formPanel.add(createFormField("Specialization", specCombo));
            formPanel.add(createFormField("Phone Number", phoneField));
            formPanel.add(createFormField("Email", emailField));
            formPanel.add(createFormField("Gender", genderCombo));
            formPanel.add(createFormField("Experience (Years)", experienceSpinner));
            formPanel.add(createFormField("Availability", new JScrollPane(availabilityArea)));
            formPanel.add(createFormField("Status", statusCombo));
            
            JScrollPane formScroll = new JScrollPane(formPanel);
            formScroll.setBackground(WHITE);
            formScroll.setBorder(null);
            editPanel.add(formScroll, BorderLayout.CENTER);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setBackground(WHITE);
            
            JButton updateBtn = new JButton("UPDATE");
            updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            updateBtn.setBackground(RED_ACCENT);
            updateBtn.setForeground(WHITE);
            updateBtn.setFocusPainted(false);
            updateBtn.setBorderPainted(false);
            updateBtn.setOpaque(true);
            updateBtn.setPreferredSize(new Dimension(100, 35));
            updateBtn.addActionListener(e -> {
                doctor.setDoctorName(nameField.getText().trim());
                doctor.setSpecialization((String) specCombo.getSelectedItem());
                doctor.setPhoneNumber(phoneField.getText().trim());
                doctor.setEmail(emailField.getText().trim());
                doctor.setGender((String) genderCombo.getSelectedItem());
                doctor.setExperienceYears((Integer) experienceSpinner.getValue());
                doctor.setAvailability(availabilityArea.getText().trim());
                doctor.setStatus((String) statusCombo.getSelectedItem());
                
                if (doctorDAO.updateDoctor(doctor)) {
                    JOptionPane.showMessageDialog(editDialog, "Doctor updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                    loadDoctors();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Failed to update doctor!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            buttonPanel.add(updateBtn);
            
            JButton cancelBtn = new JButton("CANCEL");
            cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            cancelBtn.setBackground(new Color(160, 160, 160));
            cancelBtn.setForeground(WHITE);
            cancelBtn.setFocusPainted(false);
            cancelBtn.setBorderPainted(false);
            cancelBtn.setOpaque(true);
            cancelBtn.setPreferredSize(new Dimension(100, 35));
            cancelBtn.addActionListener(e -> editDialog.dispose());
            buttonPanel.add(cancelBtn);
            
            editPanel.add(buttonPanel, BorderLayout.SOUTH);
            editDialog.add(editPanel);
            editDialog.setVisible(true);
        }
    }
    
    /**
     * Create a form field with label
     */
    private JPanel createFormField(String labelText, JComponent component) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        label.setForeground(new Color(80, 80, 80));
        
        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(component, BorderLayout.CENTER);
        
        return fieldPanel;
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
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete doctor!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Called when doctor data changes (doctor added/updated/deleted)
     * Refresh the doctor list
     */
    @Override
    public void onDoctorDataChanged() {
        loadDoctors();
    }
    
    /**
     * Called when patient data changes (patient added/updated/deleted)
     */
    @Override
    public void onPatientDataChanged() {
        // No action needed for patient changes in doctor list
    }
    
    /**
     * Called when appointment data changes (appointment added/updated/deleted)
     */
    @Override
    public void onAppointmentDataChanged() {
        // No action needed for appointment changes in doctor list
    }
}
