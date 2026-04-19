package ui;

import dao.AppointmentDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * MainDashboard class represents the main window of the Hospital Management System
 * Features a sidebar navigation with CardLayout for switching panels
 */
public class MainDashboard extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel dashboardPanel;
    private JLabel dateTimeLabel;
    private JLabel currentModuleLabel;
    
    // Sidebar buttons
    private JButton dashboardBtn;
    private JButton addPatientBtn;
    private JButton appointmentBtn;
    private JButton doctorBtn;
    
    // DAOs
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private DoctorDAO doctorDAO;
    
    // Color scheme
    private static final Color DARK_BLUE = new Color(0, 53, 102);
    private static final Color LIGHT_BLUE = new Color(30, 80, 140);
    private static final Color WHITE = Color.WHITE;
    private static final Color RED_ACCENT = new Color(214, 40, 40);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    
    /**
     * Constructor initializing the main dashboard
     */
    public MainDashboard() {
        setTitle("RBH - Hospital Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Initialize DAOs
        patientDAO = new PatientDAO();
        appointmentDAO = new AppointmentDAO();
        doctorDAO = new DoctorDAO();
        
        // Setup UI
        setupUI();
        
        // Start timer for date/time update
        startDateTimeUpdater();
        
        setVisible(true);
    }
    
    /**
     * Setup the main user interface
     */
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Top Panel with header and date/time
        mainPanel.add(createTopPanel(), BorderLayout.NORTH);
        
        // Main content area with sidebar and card panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(createSidebar(), BorderLayout.WEST);
        contentPanel.add(createCardPanel(), BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Create the top panel with header and date/time
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(DARK_BLUE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        topPanel.setPreferredSize(new Dimension(0, 60));
        
        // Left side: Hospital name
        JLabel hospitalName = new JLabel("RBH - Hospital Management System");
        hospitalName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        hospitalName.setForeground(WHITE);
        
        // Center: Current module name
        currentModuleLabel = new JLabel("Dashboard");
        currentModuleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        currentModuleLabel.setForeground(new Color(200, 200, 200));
        
        // Right side: Date and Time
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateTimeLabel.setForeground(new Color(200, 200, 200));
        
        topPanel.add(hospitalName, BorderLayout.WEST);
        topPanel.add(currentModuleLabel, BorderLayout.CENTER);
        topPanel.add(dateTimeLabel, BorderLayout.EAST);
        
        return topPanel;
    }
    
    /**
     * Create the left sidebar with navigation buttons
     */
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(DARK_BLUE);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Logo/Brand area
        JLabel logoLabel = new JLabel("RBH");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logoLabel.setForeground(RED_ACCENT);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logoLabel);
        
        JLabel taglineLabel = new JLabel("Management System");
        taglineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        taglineLabel.setForeground(new Color(180, 180, 180));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(taglineLabel);
        
        sidebar.add(Box.createVerticalStrut(40));
        
        // Navigation buttons
        dashboardBtn = createNavButton("📊 Dashboard", true);
        addPatientBtn = createNavButton("➕ Add Patient", false);
        appointmentBtn = createNavButton("📅 Appointments", false);
        
        dashboardBtn.addActionListener(e -> switchPanel("dashboard"));
        addPatientBtn.addActionListener(e -> switchPanel("addPatient"));
        appointmentBtn.addActionListener(e -> switchPanel("appointment"));
        
        sidebar.add(dashboardBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(addPatientBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(appointmentBtn);
        
        // Doctors button
        doctorBtn = createNavButton("👨‍⚕️ Doctors", false);
        doctorBtn.addActionListener(e -> switchPanel("doctor"));
        
        JButton billingBtn = createNavButton("💰 Billing", false);
        billingBtn.setEnabled(false);
        
        JButton reportsBtn = createNavButton("📈 Reports", false);
        reportsBtn.setEnabled(false);
        
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(doctorBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(billingBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(reportsBtn);
        
        sidebar.add(Box.createVerticalGlue());
        
        // Exit button
        JButton exitBtn = createNavButton("🚪 Exit", false);
        exitBtn.setBackground(new Color(150, 50, 50));
        exitBtn.addActionListener(e -> System.exit(0));
        sidebar.add(exitBtn);
        
        return sidebar;
    }
    
    /**
     * Create a styled navigation button
     */
    private JButton createNavButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setMinimumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        
        if (isActive) {
            btn.setBackground(RED_ACCENT);
            btn.setForeground(WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else {
            btn.setBackground(LIGHT_BLUE);
            btn.setForeground(WHITE);
        }
        
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(null);
        
        return btn;
    }
    
    /**
     * Create the card panel for switching between different views
     */
    private JPanel createCardPanel() {
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.setBackground(LIGHT_GRAY);
        
        // Add panels
        dashboardPanel = createDashboardPanel();
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(new AddPatientPanel(patientDAO), "addPatient");
        cardPanel.add(new AppointmentPanel(appointmentDAO, patientDAO), "appointment");
        cardPanel.add(new AddDoctorPanel(doctorDAO), "doctor");
        
        return cardPanel;
    }
    
    /**
     * Create the dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBackground(LIGHT_GRAY);
        dashboard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to RBH Hospital Management System");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(DARK_BLUE);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboard.add(welcomeLabel);
        
        dashboard.add(Box.createVerticalStrut(20));
        
        // Statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(LIGHT_GRAY);
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        int totalPatients = patientDAO.getTotalPatients();
        int totalAppointments = appointmentDAO.getTotalAppointments();
        
        statsPanel.add(createStatCard("Total Patients", String.valueOf(totalPatients), DARK_BLUE));
        statsPanel.add(createStatCard("Scheduled Appointments", String.valueOf(totalAppointments), RED_ACCENT));
        statsPanel.add(createStatCard("System Status", "Active", new Color(52, 168, 83)));
        
        dashboard.add(statsPanel);
        
        dashboard.add(Box.createVerticalStrut(40));
        
        // Quick info
        JLabel infoLabel = new JLabel("Use the sidebar to navigate through different modules:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(DARK_BLUE);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboard.add(infoLabel);
        
        dashboard.add(Box.createVerticalStrut(15));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setText("• Add Patient: Register new patients to the system\n\n" +
                        "• Appointments: Book and manage patient appointments\n\n" +
                        "• View Patient Records: Access detailed patient information\n\n" +
                        "• Track Appointments: Monitor appointment status and tokens");
        infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setForeground(new Color(80, 80, 80));
        dashboard.add(infoArea);
        
        dashboard.add(Box.createVerticalGlue());
        
        return dashboard;
    }
    
    /**
     * Create a statistic card
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        
        return card;
    }
    
    /**
     * Refresh dashboard statistics with latest data from database
     */
    private void refreshDashboard() {
        // Recreate the dashboard panel with updated statistics
        cardPanel.remove(dashboardPanel);
        dashboardPanel = createDashboardPanel();
        cardPanel.add(dashboardPanel, "dashboard");
        cardLayout.show(cardPanel, "dashboard");  // Ensure dashboard is displayed after refresh
    }
    
    /**
     * Switch to a different panel
     */
    private void switchPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        
        // Update sidebar buttons and module label
        dashboardBtn.setBackground(panelName.equals("dashboard") ? RED_ACCENT : LIGHT_BLUE);
        dashboardBtn.setFont(new Font("Segoe UI", panelName.equals("dashboard") ? Font.BOLD : Font.PLAIN, 12));
        
        addPatientBtn.setBackground(panelName.equals("addPatient") ? RED_ACCENT : LIGHT_BLUE);
        addPatientBtn.setFont(new Font("Segoe UI", panelName.equals("addPatient") ? Font.BOLD : Font.PLAIN, 12));
        
        appointmentBtn.setBackground(panelName.equals("appointment") ? RED_ACCENT : LIGHT_BLUE);
        appointmentBtn.setFont(new Font("Segoe UI", panelName.equals("appointment") ? Font.BOLD : Font.PLAIN, 12));
        
        doctorBtn.setBackground(panelName.equals("doctor") ? RED_ACCENT : LIGHT_BLUE);
        doctorBtn.setFont(new Font("Segoe UI", panelName.equals("doctor") ? Font.BOLD : Font.PLAIN, 12));
        
        // Update module label and refresh if switching to dashboard
        switch (panelName) {
            case "dashboard":
                currentModuleLabel.setText("Dashboard");
                // Refresh dashboard statistics when returning to dashboard
                refreshDashboard();
                break;
            case "addPatient":
                currentModuleLabel.setText("Add Patient");
                break;
            case "appointment":
                currentModuleLabel.setText("Appointments");
                break;
            case "doctor":
                currentModuleLabel.setText("Doctors");
                break;
        }
    }
    
    /**
     * Start background thread to update date/time
     */
    private void startDateTimeUpdater() {
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();
    }
    
    /**
     * Update the date/time display
     */
    private void updateDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy | HH:mm:ss");
        dateTimeLabel.setText(dateFormat.format(new Date()));
    }
}
