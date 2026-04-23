package ui;

import dao.AppointmentDAO;
import dao.BillDAO;
import dao.DoctorDAO;
import dao.PatientDAO;
import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import java.awt.RenderingHints;

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
    private JButton billingBtn;
    private JButton reportsBtn;
    
    // DAOs
    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private DoctorDAO doctorDAO;
    private BillDAO billDAO;
    
    // [UI CHANGE] Color scheme - Updated to match CK Birla Hospital website aesthetic
    private static final Color PRIMARY_RED = new Color(0xC8102E);          // Hospital brand red
    private static final Color DARK_GRAY = new Color(0x4A4A4A);            // Text and headings
    private static final Color STEEL_GRAY = new Color(0x607D8B);           // Secondary elements
    private static final Color LIGHT_STEEL = new Color(0x78909C);          // Tertiary elements
    private static final Color WHITE = Color.WHITE;                        // #FFFFFF
    private static final Color LIGHT_GRAY = new Color(0xF5F5F5);           // Page background
    private static final Color ACCENT_PINK = new Color(0xFFEBEE);          // Stats bar background
    private static final Color HOVER_RED = new Color(0x9B0D22);            // Button hover state
    
    // [UI CHANGE] Keeping legacy names mapped for easier maintenance
    private static final Color DARK_BLUE = PRIMARY_RED;                    // Maps to primary red
    private static final Color LIGHT_BLUE = STEEL_GRAY;                    // Maps to steel gray
    private static final Color RED_ACCENT = PRIMARY_RED;                   // Maps to primary red
    
    /**
     * Constructor initializing the main dashboard
     */
    public MainDashboard() {
        setTitle("Birla CK Hospital - Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Initialize DAOs
        patientDAO = new PatientDAO();
        appointmentDAO = new AppointmentDAO();
        doctorDAO = new DoctorDAO();
        billDAO = new BillDAO();
        
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
        // [UI CHANGE] Updated top navigation bar styling to match hospital website
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_RED);  // [UI CHANGE] Changed from DARK_BLUE to PRIMARY_RED (#C8102E)
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));  // [UI CHANGE] Adjusted padding
        topPanel.setPreferredSize(new Dimension(0, 50));  // [UI CHANGE] Reduced height for cleaner look
        
        // Left side: Logo or hospital name with fallback
        JComponent logoComponent = createHeaderLogoComponent();
        
        // Center: Current module name
        currentModuleLabel = new JLabel("Dashboard");
        currentModuleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));  // [UI CHANGE] Updated to 12pt
        currentModuleLabel.setForeground(new Color(255, 255, 255, 200));  // [UI CHANGE] More transparent white
        
        // Right side: Date and Time
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));  // [UI CHANGE] Updated to 11pt
        dateTimeLabel.setForeground(new Color(255, 255, 255, 200));  // [UI CHANGE] More transparent white
        
        topPanel.add(logoComponent, BorderLayout.WEST);
        topPanel.add(currentModuleLabel, BorderLayout.CENTER);
        topPanel.add(dateTimeLabel, BorderLayout.EAST);
        
        return topPanel;
    }
    
    /**
     * Create header logo component - displays logo image or fallback text
     */
    private JComponent createHeaderLogoComponent() {
        // Try to load rbh.webp logo image
        try {
            File logoFile = new File("rbh.webp");
            if (logoFile.exists()) {
                BufferedImage logoImage = ImageIO.read(logoFile);
                if (logoImage != null) {
                    // Scale logo to fit header (height 50px, maintain aspect ratio)
                    int maxHeight = 45;
                    int scaledWidth = (int) (logoImage.getWidth() * (maxHeight / (double) logoImage.getHeight()));
                    Image scaledImage = logoImage.getScaledInstance(scaledWidth, maxHeight, Image.SCALE_SMOOTH);
                    
                    JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
                    logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    System.out.println("✓ Successfully loaded rbh.webp logo in header");
                    return logoLabel;
                }
            }
        } catch (Exception e) {
            System.out.println("Note: Could not load rbh.webp logo - using fallback text. (" + e.getMessage() + ")");
        }
        
        // Fallback: Display "Birla CK Hospital" text
        JLabel hospitalName = new JLabel("Birla CK Hospital");
        hospitalName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hospitalName.setForeground(WHITE);
        return hospitalName;
    }
    
    /**
     * Create the left sidebar with navigation buttons
     */
    private JPanel createSidebar() {
        // [UI CHANGE] Sidebar styling for hospital website aesthetic
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(0x002D66));  // [UI CHANGE] Professional dark navy (kept for contrast)
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // [UI CHANGE] Logo/Brand area - Load rbh.webp image or display styled CK Birla logo
        JPanel logoPanel = createLogoBrandPanel();
        sidebar.add(logoPanel);
        
        // [UI CHANGE] Removed redundant "Management System" text - logo already displays hospital branding
        
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
        
        billingBtn = createNavButton("💰 Billing", false);
        billingBtn.addActionListener(e -> switchPanel("billing"));
        
        reportsBtn = createNavButton("📈 Reports", false);
        reportsBtn.addActionListener(e -> switchPanel("reports"));
        
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(doctorBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(billingBtn);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(reportsBtn);
        
        sidebar.add(Box.createVerticalGlue());
        
        // Exit button
        JButton exitBtn = createNavButton("🚪 Exit", false);
        exitBtn.setBackground(new Color(0xB71C1C));  // [UI CHANGE] Updated to darker red for exit button
        exitBtn.addActionListener(e -> System.exit(0));
        sidebar.add(exitBtn);
        
        return sidebar;
    }
    
    /**
     * [UI CHANGE] Create a custom logo panel that displays CK Birla Hospitals branding
     * Tries to load rbh.webp, falls back to styled text representation
     */
    private JPanel createLogoBrandPanel() {
        JPanel logoPanel = new JPanel() {
            private BufferedImage logoImage = null;
            
            {
                // Try to load the logo image once
                try {
                    File logoFile = new File("rbh.webp");
                    if (logoFile.exists()) {
                        logoImage = ImageIO.read(logoFile);
                        if (logoImage != null) {
                            System.out.println("✓ Successfully loaded rbh.webp logo");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Note: Could not load rbh.webp - using styled text logo. (" + e.getMessage() + ")");
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                // If logo image loaded successfully, draw it
                if (logoImage != null) {
                    int width = getWidth();
                    int height = getHeight();
                    
                    // Calculate scaling to fit within panel
                    float scaleW = (float) width / logoImage.getWidth();
                    float scaleH = (float) height / logoImage.getHeight();
                    float scale = Math.min(scaleW, scaleH) * 0.9f;
                    
                    int imgWidth = (int) (logoImage.getWidth() * scale);
                    int imgHeight = (int) (logoImage.getHeight() * scale);
                    int x = (width - imgWidth) / 2;
                    int y = (height - imgHeight) / 2;
                    
                    g2.drawImage(logoImage, x, y, imgWidth, imgHeight, this);
                } else {
                    // [UI CHANGE] Fallback: Draw styled CK Birla logo text
                    int width = getWidth();
                    int height = getHeight();
                    
                    // "CK" in red
                    g2.setColor(PRIMARY_RED);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
                    g2.drawString("CK", 20, 35);
                    
                    // "Birla Hospitals" in dark gray
                    g2.setColor(DARK_GRAY);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    g2.drawString("Birla", 65, 32);
                    
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2.drawString("Hospitals", 65, 46);
                    
                    // [UI CHANGE] Only show hospital name, removed "Management System" - redundant
                    // "RUKMANI BIRLA HOSPITAL" subtitle in gray
                    g2.setColor(new Color(0x999999));
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                    g2.drawString("RUKMANI BIRLA HOSPITAL", 20, 65);
                }
            }
        };
        
        logoPanel.setPreferredSize(new Dimension(220, 80));
        logoPanel.setMaximumSize(new Dimension(220, 80));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        return logoPanel;
    }
    
    /**
     * Create a styled navigation button
     */
    private JButton createNavButton(String text, boolean isActive) {
        // [UI CHANGE] Updated button styling for hospital website aesthetic
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setMinimumSize(new Dimension(200, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        
        if (isActive) {
            btn.setBackground(PRIMARY_RED);  // [UI CHANGE] Active button = Primary Red
            btn.setForeground(WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        } else {
            btn.setBackground(STEEL_GRAY);  // [UI CHANGE] Inactive button = Steel Gray
            btn.setForeground(WHITE);
        }
        
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBorder(null);
        
        // [UI CHANGE] Add hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(isActive ? HOVER_RED : new Color(0x546E7A));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(isActive ? PRIMARY_RED : STEEL_GRAY);
            }
        });
        
        return btn;
    }
    
    /**
     * Create the card panel for switching between different views
     */
    private JPanel createCardPanel() {
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        cardPanel.setBackground(new Color(0xF5F5F5));  // [UI CHANGE] Updated to LIGHT_GRAY (#F5F5F5) for hospital website look
        
        // Add panels
        dashboardPanel = createDashboardPanel();
        cardPanel.add(dashboardPanel, "dashboard");
        cardPanel.add(new AddPatientPanel(patientDAO), "addPatient");
        cardPanel.add(new AppointmentPanel(appointmentDAO, patientDAO), "appointment");
        cardPanel.add(new AddDoctorPanel(doctorDAO), "doctor");
        cardPanel.add(new BillingPanel(billDAO, patientDAO), "billing");
        cardPanel.add(new ReportPanel(), "reports");
        
        return cardPanel;
    }
    
    /**
     * Create the dashboard panel
     */
    private JPanel createDashboardPanel() {
        // [UI CHANGE] Updated dashboard styling for hospital website aesthetic
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBackground(new Color(0xF5F5F5));  // [UI CHANGE] Light gray background
        dashboard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to Birla Hospital");  // [UI CHANGE] Updated to "Welcome to Birla Hospital"
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(DARK_GRAY);  // [UI CHANGE] Changed to DARK_GRAY (#4A4A4A)
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboard.add(welcomeLabel);
        
        dashboard.add(Box.createVerticalStrut(20));
        
        // Statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(new Color(0xF5F5F5));  // [UI CHANGE] Updated background
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        int totalPatients = patientDAO.getTotalPatients();
        int totalAppointments = appointmentDAO.getTotalAppointments();
        
        // [UI CHANGE] Updated stat card colors to match hospital website
        statsPanel.add(createStatCard("Total Patients", String.valueOf(totalPatients), PRIMARY_RED));
        statsPanel.add(createStatCard("Scheduled Appointments", String.valueOf(totalAppointments), PRIMARY_RED));
        statsPanel.add(createStatCard("System Status", "Active", STEEL_GRAY));
        
        dashboard.add(statsPanel);
        
        dashboard.add(Box.createVerticalStrut(40));
        
        // Quick info
        JLabel infoLabel = new JLabel("Use the sidebar to navigate through different modules:");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));  // [UI CHANGE] Made bold
        infoLabel.setForeground(DARK_GRAY);  // [UI CHANGE] Changed to DARK_GRAY
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
        // [UI CHANGE] Redesigned stat card with hospital website styling
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE);  // [UI CHANGE] White background
        
        // [UI CHANGE] Updated border styling - thinner top border in card color, light bottom shadow
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, color),  // [UI CHANGE] Top border only
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));  // [UI CHANGE] 11pt from 12pt
        titleLabel.setForeground(STEEL_GRAY);  // [UI CHANGE] Steel gray for labels
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));  // [UI CHANGE] Increased to 36pt for better emphasis
        valueLabel.setForeground(color);  // [UI CHANGE] Color parameter maintained
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));  // [UI CHANGE] Reduced spacing
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
        
        // [UI CHANGE] Updated button color switching with hospital website colors
        dashboardBtn.setBackground(panelName.equals("dashboard") ? PRIMARY_RED : STEEL_GRAY);
        dashboardBtn.setFont(new Font("Segoe UI", panelName.equals("dashboard") ? Font.BOLD : Font.PLAIN, 12));
        
        addPatientBtn.setBackground(panelName.equals("addPatient") ? PRIMARY_RED : STEEL_GRAY);
        addPatientBtn.setFont(new Font("Segoe UI", panelName.equals("addPatient") ? Font.BOLD : Font.PLAIN, 12));
        
        appointmentBtn.setBackground(panelName.equals("appointment") ? PRIMARY_RED : STEEL_GRAY);
        appointmentBtn.setFont(new Font("Segoe UI", panelName.equals("appointment") ? Font.BOLD : Font.PLAIN, 12));
        
        doctorBtn.setBackground(panelName.equals("doctor") ? PRIMARY_RED : STEEL_GRAY);
        doctorBtn.setFont(new Font("Segoe UI", panelName.equals("doctor") ? Font.BOLD : Font.PLAIN, 12));
        
        if (billingBtn != null) {
            billingBtn.setBackground(panelName.equals("billing") ? RED_ACCENT : LIGHT_BLUE);
            billingBtn.setFont(new Font("Segoe UI", panelName.equals("billing") ? Font.BOLD : Font.PLAIN, 12));
        }
        
        if (reportsBtn != null) {
            reportsBtn.setBackground(panelName.equals("reports") ? PRIMARY_RED : STEEL_GRAY);
            reportsBtn.setFont(new Font("Segoe UI", panelName.equals("reports") ? Font.BOLD : Font.PLAIN, 12));
        }
        
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
            case "billing":
                currentModuleLabel.setText("Billing");
                break;
            case "reports":
                currentModuleLabel.setText("Reports & Analytics");
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
