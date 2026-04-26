package ui;

import dao.BillDAO;
import dao.PatientDAO;

import javax.swing.*;
import java.awt.*;

/**
 * BillingFrame - Opens Billing panel in a separate window
 */
public class BillingFrame extends JFrame {
    private static final Color COLOR_NAVY = new Color(27, 58, 107);
    private static final Color COLOR_PRIMARY = new Color(192, 39, 45);
    
    public BillingFrame(BillDAO billDAO, PatientDAO patientDAO) {
        setTitle("Billing");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(COLOR_NAVY);
        
        // Add header with back button at the very top
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        // Add the panel in center
        JPanel contentPanel = new BillingPanel(billDAO, patientDAO);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_NAVY);
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JButton backBtn = new JButton("← Back to Dashboard");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(COLOR_PRIMARY);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setPreferredSize(new Dimension(180, 35));
        backBtn.addActionListener(e -> dispose());
        
        header.add(backBtn, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel("Billing");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        header.add(titleLabel, BorderLayout.CENTER);
        
        return header;
    }
}
