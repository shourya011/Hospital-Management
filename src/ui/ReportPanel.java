package ui;

import dao.ReportDAO;
import model.ReportRow;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ReportPanel extends JPanel {
    // Modern CK Birla Color Palette (matches MainDashboard)
    private Color PRIMARY_COLOR = new Color(192, 39, 45);        // CK Birla red
    private Color SECONDARY_COLOR = new Color(27, 58, 107);      // Navy headings
    private Color ACCENT_COLOR = new Color(192, 39, 45);         // Primary red
    private Color BACKGROUND_COLOR = new Color(247, 248, 250);   // page background
    private Color ROW_COLOR = new Color(241, 243, 246);          // light gray
    
    // Backward compatibility aliases
    private Color DARK_BLUE = PRIMARY_COLOR;
    private Color LIGHT_GRAY = BACKGROUND_COLOR;
    private Color RED_ACCENT = PRIMARY_COLOR;
    private Color WHITE = Color.WHITE;

    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private JComboBox<String> reportTypeCombo;
    private JTable defaultTable;
    private DefaultTableModel tableModel;
    
    private JLabel totalRecordsLabel;
    private JLabel highestLabel;
    private JLabel lowestLabel;
    
    private ReportDAO reportDAO;

    public ReportPanel() {
        this.reportDAO = new ReportDAO();
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setPreferredSize(new Dimension(800, 50));
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Filter Bar Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        filterPanel.setBackground(BACKGROUND_COLOR);

        SpinnerDateModel fromModel = new SpinnerDateModel(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000), null, null, Calendar.DAY_OF_MONTH);
        SpinnerDateModel toModel = new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH);
        
        fromDateSpinner = new JSpinner(fromModel);
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd"));
        toDateSpinner = new JSpinner(toModel);
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd"));

        String[] reportTypes = {
            "Appointment Summary", 
            "Doctor-wise Appointments", 
            "Specialization-wise Appointments", 
            "Patient Demographics", 
            "Monthly Patient Registrations",
            "Total Summary Stats",
            "Billing Revenue by Mode",
            "Billing Summary Stats"
        };
        reportTypeCombo = new JComboBox<>(reportTypes);

        JButton generateBtn = createStyledButton("Generate Report", PRIMARY_COLOR);
        JButton exportBtn = createStyledButton("Export CSV", SECONDARY_COLOR);

        filterPanel.add(new JLabel("From Date:"));
        filterPanel.add(fromDateSpinner);
        filterPanel.add(new JLabel("To Date:"));
        filterPanel.add(toDateSpinner);
        filterPanel.add(new JLabel("Report Type:"));
        filterPanel.add(reportTypeCombo);
        filterPanel.add(generateBtn);
        filterPanel.add(exportBtn);

        // Center Table Area
        String[] columns = {"Category", "Count"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        defaultTable = new JTable(tableModel);
        defaultTable.setRowHeight(28);
        defaultTable.getTableHeader().setBackground(PRIMARY_COLOR);
        defaultTable.getTableHeader().setForeground(Color.WHITE);
        defaultTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        
        // Alternating row colors
        defaultTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : ROW_COLOR);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(defaultTable);
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(BACKGROUND_COLOR);
        centerWrapper.add(filterPanel, BorderLayout.NORTH);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);
        add(centerWrapper, BorderLayout.CENTER);

        // Bottom Summary Bar
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        summaryPanel.setBackground(BACKGROUND_COLOR);
        
        totalRecordsLabel = createSummaryCard("Total Records: 0");
        highestLabel = createSummaryCard("Highest: N/A");
        lowestLabel = createSummaryCard("Lowest: N/A");
        
        summaryPanel.add(totalRecordsLabel);
        summaryPanel.add(highestLabel);
        summaryPanel.add(lowestLabel);
        add(summaryPanel, BorderLayout.SOUTH);

        // Event Listeners
        reportTypeCombo.addActionListener(e -> {
            String selected = (String) reportTypeCombo.getSelectedItem();
            boolean needDates = !("Patient Demographics".equals(selected) || "Monthly Patient Registrations".equals(selected));
            fromDateSpinner.setEnabled(needDates);
            toDateSpinner.setEnabled(needDates);
            // Optionally auto-generate when switching type
            generateReport();
        });

        generateBtn.addActionListener(e -> generateReport());
        exportBtn.addActionListener(e -> exportCsv());
        
        // Generate initial report upon opening the panel
        generateReport();
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        return btn;
    }

    private JLabel createSummaryCard(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(SECONDARY_COLOR);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setBorder(new EmptyBorder(10, 20, 10, 20));
        return label;
    }

    private void generateReport() {
        String type = (String) reportTypeCombo.getSelectedItem();
        Date from = (Date) fromDateSpinner.getValue();
        Date to = (Date) toDateSpinner.getValue();
        
        ArrayList<ReportRow> results = new ArrayList<>();
        
        if ("Appointment Summary".equals(type)) {
            results = reportDAO.getAppointmentSummary(from, to);
        } else if ("Doctor-wise Appointments".equals(type)) {
            results = reportDAO.getDoctorWiseAppointments(from, to);
        } else if ("Specialization-wise Appointments".equals(type)) {
            results = reportDAO.getSpecializationWiseAppointments(from, to);
        } else if ("Patient Demographics".equals(type)) {
            results = reportDAO.getPatientGenderDistribution();
        } else if ("Monthly Patient Registrations".equals(type)) {
            // Assume current year if not specified, parsing from "From Date" roughly
            Calendar cal = Calendar.getInstance();
            cal.setTime(from);
            results = reportDAO.getMonthlyRegistrations(cal.get(Calendar.YEAR));
        } else if ("Total Summary Stats".equals(type)) {
            results = reportDAO.getTotalSummaryStats(from, to);
        } else if ("Billing Revenue by Mode".equals(type)) {
            results = reportDAO.getBillingRevenueByMode();
        } else if ("Billing Summary Stats".equals(type)) {
            // Build ad-hoc billing summary as ReportRow list
            results = new java.util.ArrayList<>();
            results.add(new model.ReportRow("Total Revenue (Paid)",
                (int) reportDAO.getBillingTotalRevenue()));
            results.add(new model.ReportRow("Pending Payments Count",
                reportDAO.getBillingPendingCount()));
            results.add(new model.ReportRow("Today's Collections",
                (int) reportDAO.getBillingTodayCollections()));
        }

        tableModel.setRowCount(0);
        int total = 0, max = -1, min = Integer.MAX_VALUE;
        String maxLbl = "N/A", minLbl = "N/A";
        
        for (ReportRow row : results) {
            tableModel.addRow(new Object[]{row.getLabel(), row.getValue()});
            total += row.getValue();
            if (row.getValue() > max) { max = row.getValue(); maxLbl = row.getLabel() + " (" + max + ")"; }
            if (row.getValue() < min) { min = row.getValue(); minLbl = row.getLabel() + " (" + min + ")"; }
        }
        
        if (results.isEmpty()) { minLbl = "N/A"; }
        
        totalRecordsLabel.setText("Total Records: " + total);
        highestLabel.setText("Highest: " + maxLbl);
        lowestLabel.setText("Lowest: " + minLbl);
    }

    private void exportCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");   
        int userSelection = fileChooser.showSaveDialog(this);
         
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.append("Category,Count\n");
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.append(tableModel.getValueAt(i, 0).toString())
                          .append(",")
                          .append(tableModel.getValueAt(i, 1).toString())
                          .append("\n");
                }
                JOptionPane.showMessageDialog(this, "File saved successfully!", "Export CSV", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Export CSV", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}