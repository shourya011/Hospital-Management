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
    
    // Navbar buttons
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
    
    // Modern CK Birla Hospitals Color Palette
    private static final Color COLOR_PRIMARY = new Color(192, 39, 45);       // CK Birla red (#C0272D)
    private static final Color COLOR_PRIMARY_DARK = new Color(155, 29, 34);  // hover/pressed red (#9B1D22)
    private static final Color COLOR_NAVY = new Color(27, 58, 107);          // headings, navbar (#1B3A6B)
    private static final Color COLOR_BG_PAGE = new Color(247, 248, 250);     // frame/panel bg (#F7F8FA)
    private static final Color COLOR_BG_CARD = Color.WHITE;                  // card background
    private static final Color COLOR_TEXT_BODY = new Color(74, 74, 74);      // general text (#4A4A4A)
    private static final Color COLOR_BORDER = new Color(229, 231, 235);      // panel borders (#E5E7EB)
    private static final Color COLOR_SUCCESS = new Color(29, 158, 117);      // active status (#1D9E75)
    private static final Color COLOR_TEXT_MUTED = new Color(150, 150, 160);  // secondary labels
    
    // Font Definitions
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_SUBHEAD = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_LOGO = new Font("Segoe UI", Font.BOLD, 16);
    
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
        
        setVisible(true);
    }
    
    /**
     * Setup the main user interface with modern top navbar layout
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Top Navigation Bar
        add(createTopNavbar(), BorderLayout.NORTH);
        
        // Main Content Area (scrollable)
        JScrollPane mainScrollPane = new JScrollPane(createMainContent());
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScrollPane, BorderLayout.CENTER);
        
        // Footer Bar
        add(createFooter(), BorderLayout.SOUTH);
    }
    
    /**
     * Create the modern top navigation bar
     */
    private JPanel createTopNavbar() {
        JPanel navBar = new JPanel(new BorderLayout(16, 0));
        navBar.setBackground(COLOR_NAVY);
        navBar.setPreferredSize(new Dimension(0, 68));
        navBar.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        // Left: Logo Image with Hospital Name
        JPanel logoArea = new JPanel();
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.X_AXIS));
        logoArea.setBackground(COLOR_NAVY);
        logoArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        // Load and add logo image (40x40)
        JLabel logoImg = new JLabel();
        try {
            File logoFile = new File("download.png");
            if (logoFile.exists()) {
                BufferedImage logoBuffer = ImageIO.read(logoFile);
                Image scaledImg = logoBuffer.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                logoImg.setIcon(new ImageIcon(scaledImg));
            }
        } catch (Exception e) {
            // Logo not found, continue without it
        }
        logoArea.add(logoImg);
        logoArea.add(Box.createHorizontalStrut(10));
        
        // Add text labels stacked vertically
        JPanel logoText = new JPanel();
        logoText.setLayout(new BoxLayout(logoText, BoxLayout.Y_AXIS));
        logoText.setBackground(COLOR_NAVY);
        
        JLabel logoTitle = new JLabel("CK Birla Hospitals");
        logoTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoTitle.setForeground(Color.WHITE);
        
        JLabel logoSub = new JLabel("RUKMANI BIRLA HOSPITAL");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        logoSub.setForeground(new Color(180, 190, 210));
        
        logoText.add(logoTitle);
        logoText.add(logoSub);
        logoArea.add(logoText);
        
        navBar.add(logoArea, BorderLayout.WEST);
        
        // Center: Navigation Links
        JPanel navLinksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        navLinksPanel.setOpaque(false);
        
        // Dashboard
        dashboardBtn = createNavLink("Dashboard");
        dashboardBtn.addActionListener(e -> switchPanel("dashboard"));
        navLinksPanel.add(dashboardBtn);
        
        // Add Patient
        addPatientBtn = createNavLink("Add Patient");
        addPatientBtn.addActionListener(e -> switchPanel("addPatient"));
        navLinksPanel.add(addPatientBtn);
        
        // Appointments
        appointmentBtn = createNavLink("Appointments");
        appointmentBtn.addActionListener(e -> switchPanel("appointment"));
        navLinksPanel.add(appointmentBtn);
        
        // Doctors
        doctorBtn = createNavLink("Doctors");
        doctorBtn.addActionListener(e -> switchPanel("doctor"));
        navLinksPanel.add(doctorBtn);
        
        // Billing
        billingBtn = createNavLink("Billing");
        billingBtn.addActionListener(e -> switchPanel("billing"));
        navLinksPanel.add(billingBtn);
        
        // Reports
        reportsBtn = createNavLink("Reports");
        reportsBtn.addActionListener(e -> switchPanel("reports"));
        navLinksPanel.add(reportsBtn);
        
        navBar.add(navLinksPanel, BorderLayout.CENTER);
        
        // Right: Status Badge and Actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        
        JLabel statusBadge = new JLabel("● Active");
        statusBadge.setForeground(COLOR_SUCCESS);
        statusBadge.setFont(FONT_SMALL);
        rightPanel.add(statusBadge);
        
        rightPanel.add(Box.createHorizontalStrut(8));
        
        JButton logoutBtn = new JButton("⏻");
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setContentAreaFilled(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> System.exit(0));
        rightPanel.add(logoutBtn);
        
        navBar.add(rightPanel, BorderLayout.EAST);
        
        return navBar;
    }
    
    /**
     * Create a navigation link button (unstyled initially, styled on nav change)
     */
    private JButton createNavLink(String text) {
        JButton btn = new JButton(text);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BODY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(new Color(255, 200, 200));  // Light red on hover
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
        });
        
        return btn;
    }
    
    /**
     * Create the main content area (stacked vertically)
     */
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BG_PAGE);
        
        // Hero Panel (Dashboard only)
        JPanel heroPanel = createHeroPanel();
        mainPanel.add(heroPanel);
        
        // Metrics Strip (Dashboard only)
        JPanel metricsStrip = createMetricsStrip();
        mainPanel.add(metricsStrip);
        
        // Action Cards Section (Dashboard only)
        JPanel actionSection = createActionSection();
        mainPanel.add(actionSection);
        
        // Content Area with CardLayout (always visible)
        cardPanel = createCardPanel();
        mainPanel.add(cardPanel);
        
        return mainPanel;
    }
    
    /**
     * Create hero panel for dashboard welcome section with hospital image
     */
    private JPanel createHeroPanel() {
        JPanel hero = new JPanel(new BorderLayout());
        hero.setBackground(COLOR_BG_PAGE);
        hero.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        
        // Left content
        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setOpaque(false);
        
        JLabel pretitle = new JLabel("Birla Hospital System");
        pretitle.setFont(FONT_SMALL);
        pretitle.setForeground(COLOR_PRIMARY);
        pretitle.setText(pretitle.getText().toUpperCase());
        leftContent.add(pretitle);
        
        JLabel welcomeTitle = new JLabel("Welcome to Birla Hospital");
        welcomeTitle.setFont(FONT_TITLE);
        welcomeTitle.setForeground(COLOR_NAVY);
        leftContent.add(Box.createVerticalStrut(8));
        leftContent.add(welcomeTitle);
        
        JLabel subtitle = new JLabel("<html>Manage patients, appointments, doctors, billing and reports from one place.</html>");
        subtitle.setFont(FONT_BODY);
        subtitle.setForeground(COLOR_TEXT_BODY);
        leftContent.add(Box.createVerticalStrut(12));
        leftContent.add(subtitle);
        
        leftContent.add(Box.createVerticalStrut(16));
        
        // Buttons panel (no logo here)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonPanel.setOpaque(false);
        
        JButton getStartedBtn = createPrimaryButton("Get Started");
        getStartedBtn.addActionListener(e -> switchPanel("addPatient"));
        buttonPanel.add(getStartedBtn);
        
        JButton viewReportsBtn = new JButton("View Reports");
        viewReportsBtn.setFont(FONT_SUBHEAD);
        viewReportsBtn.setForeground(COLOR_PRIMARY);
        viewReportsBtn.setBackground(COLOR_BG_PAGE);
        viewReportsBtn.setOpaque(true);
        viewReportsBtn.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 2));
        viewReportsBtn.setFocusPainted(false);
        viewReportsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewReportsBtn.addActionListener(e -> switchPanel("reports"));
        buttonPanel.add(viewReportsBtn);
        
        leftContent.add(buttonPanel);
        
        hero.add(leftContent, BorderLayout.WEST);
        
        // Right content - Hospital Image or Medical Graphic
        JPanel rightContent = new JPanel() {
            private BufferedImage hospitalImage = null;
            
            {
                // Try to load hospital image
                try {
                    File imageFile = new File("download.jpeg");
                    if (imageFile.exists()) {
                        hospitalImage = ImageIO.read(imageFile);
                        System.out.println("✓ Hospital image loaded successfully");
                    }
                } catch (Exception e) {
                    System.out.println("Note: Could not load hospital image - using medical graphic instead");
                }
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (hospitalImage != null) {
                    // Draw hospital image scaled to fit
                    int maxWidth = getWidth() - 20;
                    int maxHeight = getHeight() - 20;
                    float scaleW = (float) maxWidth / hospitalImage.getWidth();
                    float scaleH = (float) maxHeight / hospitalImage.getHeight();
                    float scale = Math.min(scaleW, scaleH);
                    
                    int imgWidth = (int) (hospitalImage.getWidth() * scale);
                    int imgHeight = (int) (hospitalImage.getHeight() * scale);
                    int x = (getWidth() - imgWidth) / 2;
                    int y = (getHeight() - imgHeight) / 2;
                    
                    g2.drawImage(hospitalImage, x, y, imgWidth, imgHeight, this);
                } else {
                    // Fallback: Draw medical cross symbol
                    g2.setColor(new Color(192, 39, 45, 50));
                    g2.setStroke(new BasicStroke(6));
                    int size = 60;
                    int x = (getWidth() - size) / 2;
                    int y = (getHeight() - size) / 2;
                    g2.drawLine(x + size / 2, y, x + size / 2, y + size);
                    g2.drawLine(x, y + size / 2, x + size, y + size / 2);
                }
            }
        };
        rightContent.setOpaque(false);
        hero.add(rightContent, BorderLayout.CENTER);
        
        return hero;
    }
    
    /**
     * Create metrics strip with stat cards
     */
    private JPanel createMetricsStrip() {
        JPanel strip = new JPanel(new GridLayout(1, 3, 16, 0));
        strip.setBackground(COLOR_BG_PAGE);
        strip.setBorder(BorderFactory.createEmptyBorder(0, 40, 24, 40));
        strip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        
        int totalPatients = patientDAO.getTotalPatients();
        int totalAppointments = appointmentDAO.getTotalAppointments();
        
        // Total Patients Card
        JPanel patientCard = createCard();
        patientCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, COLOR_PRIMARY),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        JLabel patientIcon = new JLabel("👥");
        patientIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        JLabel patientValue = new JLabel(String.valueOf(totalPatients));
        patientValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        patientValue.setForeground(COLOR_PRIMARY);
        JLabel patientLabel = new JLabel("Total Patients");
        patientLabel.setFont(FONT_SMALL);
        patientLabel.setForeground(COLOR_TEXT_MUTED);
        patientCard.setLayout(new BoxLayout(patientCard, BoxLayout.Y_AXIS));
        patientCard.add(patientIcon);
        patientCard.add(Box.createVerticalStrut(8));
        patientCard.add(patientValue);
        patientCard.add(Box.createVerticalStrut(4));
        patientCard.add(patientLabel);
        strip.add(patientCard);
        
        // Scheduled Appointments Card
        JPanel appointmentCard = createCard();
        appointmentCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, COLOR_PRIMARY),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        JLabel appointmentIcon = new JLabel("📅");
        appointmentIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        JLabel appointmentValue = new JLabel(String.valueOf(totalAppointments));
        appointmentValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        appointmentValue.setForeground(COLOR_PRIMARY);
        JLabel appointmentLabel = new JLabel("Scheduled Appointments");
        appointmentLabel.setFont(FONT_SMALL);
        appointmentLabel.setForeground(COLOR_TEXT_MUTED);
        appointmentCard.setLayout(new BoxLayout(appointmentCard, BoxLayout.Y_AXIS));
        appointmentCard.add(appointmentIcon);
        appointmentCard.add(Box.createVerticalStrut(8));
        appointmentCard.add(appointmentValue);
        appointmentCard.add(Box.createVerticalStrut(4));
        appointmentCard.add(appointmentLabel);
        strip.add(appointmentCard);
        
        // System Status Card
        JPanel statusCard = createCard();
        statusCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(3, 0, 0, 0, COLOR_SUCCESS),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        JLabel statusIcon = new JLabel("✅");
        statusIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        JLabel statusValue = new JLabel("Active");
        statusValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        statusValue.setForeground(COLOR_SUCCESS);
        JLabel statusLabel = new JLabel("System Status");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(COLOR_TEXT_MUTED);
        statusCard.setLayout(new BoxLayout(statusCard, BoxLayout.Y_AXIS));
        statusCard.add(statusIcon);
        statusCard.add(Box.createVerticalStrut(8));
        statusCard.add(statusValue);
        statusCard.add(Box.createVerticalStrut(4));
        statusCard.add(statusLabel);
        strip.add(statusCard);
        
        return strip;
    }
    
    /**
     * Create action cards section - all cards fully clickable
     */
    private JPanel createActionSection() {
        JPanel section = new JPanel(new GridLayout(1, 3, 16, 0));
        section.setBackground(COLOR_BG_PAGE);
        section.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        
        // Card 1: Find a Doctor (Red) - Fully clickable
        section.add(createActionCard(COLOR_PRIMARY, "Find a Doctor", "Browse by name or specialty", "→", () -> switchPanel("doctor")));
        
        // Card 2: Health Check-up (Light Gray) - Fully clickable
        section.add(createActionCard(new Color(241, 243, 246), "Health Check-up", "Schedule your check-up today", "", () -> switchPanel("appointment")));
        
        // Card 3: Book Appointment (Slate) - Fully clickable
        section.add(createActionCard(new Color(220, 228, 240), "Book Appointment", "Get expert care whenever you need", "", () -> switchPanel("appointment")));
        
        return section;
    }
    
    /**
     * Helper method to create a clickable action card
     */
    private JPanel createActionCard(Color bgColor, String title, String subtitle, String arrow, Runnable onClicked) {
        // Create a custom JPanel that properly captures mouse events
        JPanel card = new JPanel() {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw slight hover effect
                if (isHovered) {
                    g2.setColor(new Color(0, 0, 0, 10));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add mouse listener for hover and click effects
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onClicked.run();
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setOpaque(false);
                card.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setOpaque(true);
                card.repaint();
            }
        });
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FONT_HEADING);
        Color titleColor = bgColor.equals(COLOR_PRIMARY) ? Color.WHITE : COLOR_NAVY;
        titleLabel.setForeground(titleColor);
        card.add(titleLabel);
        
        card.add(Box.createVerticalStrut(4));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(FONT_SMALL);
        Color subtitleColor = bgColor.equals(COLOR_PRIMARY) ? new Color(255, 200, 200) : COLOR_TEXT_MUTED;
        subtitleLabel.setForeground(subtitleColor);
        card.add(subtitleLabel);
        
        card.add(Box.createVerticalGlue());
        
        // Arrow (if provided)
        if (!arrow.isEmpty()) {
            JLabel arrowLabel = new JLabel(arrow);
            arrowLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            arrowLabel.setForeground(Color.WHITE);
            card.add(arrowLabel);
        }
        
        return card;
    }
    
    /**
     * Create the footer bar
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(COLOR_NAVY);
        footer.setPreferredSize(new Dimension(0, 48));
        footer.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 24));
        
        JLabel leftLabel = new JLabel("CK Birla Hospitals");
        leftLabel.setFont(FONT_SMALL);
        leftLabel.setForeground(Color.WHITE);
        footer.add(leftLabel, BorderLayout.WEST);
        
        JLabel centerLabel = new JLabel("© 2026 Rukmani Birla Hospital");
        centerLabel.setFont(FONT_SMALL);
        centerLabel.setForeground(Color.WHITE);
        footer.add(centerLabel, BorderLayout.CENTER);
        
        JLabel rightLabel = new JLabel("Emergency: 07340054470");
        rightLabel.setFont(FONT_SMALL);
        rightLabel.setForeground(COLOR_PRIMARY);
        footer.add(rightLabel, BorderLayout.EAST);
        
        return footer;
    }
    
    /**
     * Helper method: Create a card panel with standard styling
     */
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(COLOR_BG_CARD);
        card.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        return card;
    }
    
    /**
     * Helper method: Create a primary action button
     */
    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(COLOR_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_SUBHEAD);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARY_DARK);
            }
            
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_PRIMARY);
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
        cardPanel.setBackground(COLOR_BG_PAGE);
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        
        // Add only dashboard panel - other modules will open in separate windows
        cardPanel.add(createDashboardPanel(), "dashboard");
        cardPanel.add(new JPanel(), "addPatient");      // Placeholder
        cardPanel.add(new JPanel(), "appointment");     // Placeholder
        cardPanel.add(new JPanel(), "doctor");          // Placeholder
        cardPanel.add(new JPanel(), "billing");         // Placeholder
        cardPanel.add(new JPanel(), "reports");         // Placeholder
        
        return cardPanel;
    }
    
    /**
     * Create the dashboard panel - statistics and welcome content
     */
    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setBackground(COLOR_BG_PAGE);
        dashboard.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        JLabel welcomeLabel = new JLabel("Hospital Management Overview");
        welcomeLabel.setFont(FONT_HEADING);
        welcomeLabel.setForeground(COLOR_NAVY);
        dashboard.add(welcomeLabel);
        
        dashboard.add(Box.createVerticalStrut(24));
        
        JLabel infoLabel = new JLabel("Quick information about the system:");
        infoLabel.setFont(FONT_BODY);
        infoLabel.setForeground(COLOR_TEXT_BODY);
        dashboard.add(infoLabel);
        
        dashboard.add(Box.createVerticalStrut(16));
        
        JTextArea infoArea = new JTextArea();
        infoArea.setText("• Add Patient: Register new patients to the system\n\n" +
                        "• Appointments: Book and manage patient appointments\n\n" +
                        "• View Patient Records: Access detailed patient information\n\n" +
                        "• Track Appointments: Monitor appointment status and tokens\n\n" +
                        "• Doctors: Manage doctor information and specialties\n\n" +
                        "• Billing: Process patient bills and payments\n\n" +
                        "• Reports: View analytics and system reports");
        infoArea.setFont(FONT_BODY);
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setForeground(COLOR_TEXT_BODY);
        dashboard.add(infoArea);
        
        dashboard.add(Box.createVerticalGlue());
        
        return dashboard;
    }
    
    /**
     * Refresh dashboard statistics with latest data from database
     */
    private void refreshDashboard() {
        // Recreate the dashboard panel with updated statistics
        cardPanel.removeAll();
        cardPanel.add(createDashboardPanel(), "dashboard");
        cardPanel.add(new JPanel(), "addPatient");      // Placeholder
        cardPanel.add(new JPanel(), "appointment");     // Placeholder
        cardPanel.add(new JPanel(), "doctor");          // Placeholder
        cardPanel.add(new JPanel(), "billing");         // Placeholder
        cardPanel.add(new JPanel(), "reports");         // Placeholder
        cardLayout.show(cardPanel, "dashboard");
    }
    
    /**
     * Switch to a different panel and update navbar highlighting
     * For non-dashboard modules, opens a separate window
     */
    private void switchPanel(String panelName) {
        // Update nav button styling based on active panel
        resetNavButtonStyles();
        
        switch (panelName) {
            case "dashboard":
                cardLayout.show(cardPanel, panelName);
                dashboardBtn.setForeground(COLOR_PRIMARY);
                dashboardBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                refreshDashboard();
                break;
            case "addPatient":
                addPatientBtn.setForeground(COLOR_PRIMARY);
                addPatientBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                new AddPatientFrame(patientDAO);
                break;
            case "appointment":
                appointmentBtn.setForeground(COLOR_PRIMARY);
                appointmentBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                new AppointmentFrame(appointmentDAO, patientDAO);
                break;
            case "doctor":
                doctorBtn.setForeground(COLOR_PRIMARY);
                doctorBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                new AddDoctorFrame(doctorDAO);
                break;
            case "billing":
                billingBtn.setForeground(COLOR_PRIMARY);
                billingBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                new BillingFrame(billDAO, patientDAO);
                break;
            case "reports":
                reportsBtn.setForeground(COLOR_PRIMARY);
                reportsBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                new ReportFrame();
                break;
        }
    }
    
    /**
     * Reset all nav buttons to default styling
     */
    private void resetNavButtonStyles() {
        dashboardBtn.setForeground(Color.WHITE);
        dashboardBtn.setFont(FONT_BODY);
        addPatientBtn.setForeground(Color.WHITE);
        addPatientBtn.setFont(FONT_BODY);
        appointmentBtn.setForeground(Color.WHITE);
        appointmentBtn.setFont(FONT_BODY);
        doctorBtn.setForeground(Color.WHITE);
        doctorBtn.setFont(FONT_BODY);
        billingBtn.setForeground(Color.WHITE);
        billingBtn.setFont(FONT_BODY);
        reportsBtn.setForeground(Color.WHITE);
        reportsBtn.setFont(FONT_BODY);
    }
}
