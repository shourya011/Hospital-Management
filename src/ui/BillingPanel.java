package ui;

import dao.BillDAO;
import dao.PatientDAO;
import model.Bill;
import model.Patient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * BillingPanel for generating and managing patient bills.
 * Implements DataChangeListener to refresh when patient data changes.
 */
public class BillingPanel extends JPanel implements DataChangeListener {

    private BillDAO billDAO;
    private PatientDAO patientDAO;
    private DataChangeManager dataChangeManager;

    // Form fields
    private JTextField patientPhoneField;
    private JTextField patientNameField;
    private JTextField consultationField;
    private JTextField medicineField;
    private JTextField roomField;
    private JTextField labField;
    private JTextField otherField;
    private JTextField discountField;
    private JTextField taxField;
    private JLabel subtotalLabel;
    private JLabel grandTotalLabel;
    private JComboBox<String> paymentStatusCombo;
    private JComboBox<String> paymentModeCombo;
    private JTextArea notesArea;

    // Table
    private JTable billTable;
    private DefaultTableModel tableModel;

    // State
    private int selectedPatientId = -1;
    private int selectedBillId    = -1;

    // Colors — match project theme exactly
    private static final Color DARK_BLUE  = new Color(0, 53, 102);
    private static final Color LIGHT_BLUE = new Color(30, 80, 140);
    private static final Color RED_ACCENT = new Color(214, 40, 40);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color WHITE      = Color.WHITE;
    private static final Color GREEN      = new Color(52, 168, 83);
    private static final Color ORANGE     = new Color(230, 126, 34);

    public BillingPanel(BillDAO billDAO, PatientDAO patientDAO) {
        this.billDAO           = billDAO;
        this.patientDAO        = patientDAO;
        this.dataChangeManager = DataChangeManager.getInstance();
        dataChangeManager.addListener(this);

        setLayout(new BorderLayout());
        setBackground(LIGHT_GRAY);

        add(createTitlePanel(), BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(10, 10));
        mainContent.setBackground(LIGHT_GRAY);
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainContent.add(createFormPanel(), BorderLayout.WEST);
        mainContent.add(createTablePanel(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
        loadBills();
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private JPanel createTitlePanel() {
        JPanel p = new JPanel();
        p.setBackground(DARK_BLUE);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lbl = new JLabel("💰 Billing Management");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(WHITE);
        p.add(lbl);
        return p;
    }

    // ── Form ──────────────────────────────────────────────────────────────────

    private JPanel createFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        form.setPreferredSize(new Dimension(370, 0));

        // ── Patient lookup ────────────────────────────────────────────────────
        form.add(sectionLabel("Patient Information"));

        JPanel phoneRow = new JPanel(new BorderLayout(8, 0));
        phoneRow.setOpaque(false);
        patientPhoneField = styledField();
        JButton findBtn = actionButton("FIND PATIENT", DARK_BLUE);
        findBtn.addActionListener(e -> findPatient());
        JPanel phoneSub = new JPanel(new BorderLayout());
        phoneSub.setOpaque(false);
        phoneSub.add(miniLabel("Patient Phone"), BorderLayout.NORTH);
        phoneSub.add(patientPhoneField, BorderLayout.CENTER);
        phoneRow.add(phoneSub, BorderLayout.CENTER);
        phoneRow.add(findBtn, BorderLayout.EAST);
        form.add(phoneRow);
        form.add(Box.createVerticalStrut(8));

        JPanel nameRow = labeledField("Patient Name");
        patientNameField = (JTextField) nameRow.getComponent(1);
        patientNameField.setEditable(false);
        patientNameField.setBackground(new Color(230, 230, 230));
        form.add(nameRow);
        form.add(Box.createVerticalStrut(14));

        // ── Charges ───────────────────────────────────────────────────────────
        form.add(sectionLabel("Charge Breakdown"));

        JPanel cRow = labeledField("Consultation Fee (₹)");
        consultationField = (JTextField) cRow.getComponent(1);
        consultationField.setText("0");
        addAutoCalc(consultationField);
        form.add(cRow);
        form.add(Box.createVerticalStrut(6));

        JPanel mRow = labeledField("Medicine Charges (₹)");
        medicineField = (JTextField) mRow.getComponent(1);
        medicineField.setText("0");
        addAutoCalc(medicineField);
        form.add(mRow);
        form.add(Box.createVerticalStrut(6));

        JPanel rRow = labeledField("Room Charges (₹)");
        roomField = (JTextField) rRow.getComponent(1);
        roomField.setText("0");
        addAutoCalc(roomField);
        form.add(rRow);
        form.add(Box.createVerticalStrut(6));

        JPanel lRow = labeledField("Lab Charges (₹)");
        labField = (JTextField) lRow.getComponent(1);
        labField.setText("0");
        addAutoCalc(labField);
        form.add(lRow);
        form.add(Box.createVerticalStrut(6));

        JPanel oRow = labeledField("Other Charges (₹)");
        otherField = (JTextField) oRow.getComponent(1);
        otherField.setText("0");
        addAutoCalc(otherField);
        form.add(oRow);
        form.add(Box.createVerticalStrut(10));

        // ── Subtotal display ──────────────────────────────────────────────────
        subtotalLabel = new JLabel("Subtotal: ₹ 0.00");
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        subtotalLabel.setForeground(DARK_BLUE);
        form.add(subtotalLabel);
        form.add(Box.createVerticalStrut(10));

        // ── Discount & Tax ────────────────────────────────────────────────────
        JPanel dRow = labeledField("Discount (₹)");
        discountField = (JTextField) dRow.getComponent(1);
        discountField.setText("0");
        addAutoCalc(discountField);
        form.add(dRow);
        form.add(Box.createVerticalStrut(6));

        JPanel tRow = labeledField("Tax / GST (₹)");
        taxField = (JTextField) tRow.getComponent(1);
        taxField.setText("0");
        addAutoCalc(taxField);
        form.add(tRow);
        form.add(Box.createVerticalStrut(10));

        // ── Grand total ───────────────────────────────────────────────────────
        grandTotalLabel = new JLabel("Grand Total: ₹ 0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        grandTotalLabel.setForeground(RED_ACCENT);
        form.add(grandTotalLabel);
        form.add(Box.createVerticalStrut(12));

        // ── Payment ───────────────────────────────────────────────────────────
        form.add(sectionLabel("Payment Details"));

        JPanel statusRow = new JPanel(new BorderLayout());
        statusRow.setOpaque(false);
        statusRow.add(miniLabel("Payment Status"), BorderLayout.NORTH);
        paymentStatusCombo = new JComboBox<>(new String[]{"Pending", "Paid", "Partially Paid"});
        paymentStatusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusRow.add(paymentStatusCombo, BorderLayout.CENTER);
        form.add(statusRow);
        form.add(Box.createVerticalStrut(8));

        JPanel modeRow = new JPanel(new BorderLayout());
        modeRow.setOpaque(false);
        modeRow.add(miniLabel("Payment Mode"), BorderLayout.NORTH);
        paymentModeCombo = new JComboBox<>(new String[]{"Cash", "Card", "UPI", "Insurance"});
        paymentModeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        modeRow.add(paymentModeCombo, BorderLayout.CENTER);
        form.add(modeRow);
        form.add(Box.createVerticalStrut(8));

        JPanel notesRow = new JPanel(new BorderLayout());
        notesRow.setOpaque(false);
        notesRow.add(miniLabel("Notes"), BorderLayout.NORTH);
        notesArea = new JTextArea(2, 20);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesRow.add(new JScrollPane(notesArea), BorderLayout.CENTER);
        form.add(notesRow);
        form.add(Box.createVerticalStrut(14));

        // ── Buttons ───────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        btnPanel.setOpaque(false);
        JButton generateBtn = actionButton("GENERATE BILL", RED_ACCENT);
        generateBtn.addActionListener(e -> generateBill());
        JButton clearBtn = actionButton("CLEAR", new Color(140, 140, 140));
        clearBtn.addActionListener(e -> clearForm());
        btnPanel.add(generateBtn);
        btnPanel.add(clearBtn);
        form.add(btnPanel);
        form.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(LIGHT_GRAY);
        wrap.add(scroll);
        return wrap;
    }

    // ── Table ─────────────────────────────────────────────────────────────────

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("All Bills");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(DARK_BLUE);
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Bill No", "Patient", "Date", "Subtotal", "Discount", "Tax", "Total", "Mode", "Status"};
        tableModel = new DefaultTableModel(new Object[0][0], cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        billTable = new JTable(tableModel);
        billTable.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        billTable.setRowHeight(24);
        billTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billTable.getTableHeader().setBackground(DARK_BLUE);
        billTable.getTableHeader().setForeground(WHITE);
        billTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Color-code status column
        billTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String status = v == null ? "" : v.toString();
                if (!sel) {
                    switch (status) {
                        case "Paid":           setBackground(new Color(212, 237, 218)); break;
                        case "Pending":        setBackground(new Color(255, 243, 205)); break;
                        case "Partially Paid": setBackground(new Color(255, 220, 190)); break;
                        default:               setBackground(WHITE);
                    }
                }
                setForeground(sel ? WHITE : Color.BLACK);
                return this;
            }
        });

        billTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent e) {
                int row = billTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    billTable.setRowSelectionInterval(row, row);
                    selectedBillId = getBillIdForRow(row);
                    if (e.isPopupTrigger()) showContextMenu(e.getComponent(), e.getX(), e.getY(), row);
                }
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
                int row = billTable.rowAtPoint(e.getPoint());
                if (row >= 0 && e.isPopupTrigger()) {
                    billTable.setRowSelectionInterval(row, row);
                    selectedBillId = getBillIdForRow(row);
                    showContextMenu(e.getComponent(), e.getX(), e.getY(), row);
                }
            }
        });

        panel.add(new JScrollPane(billTable), BorderLayout.CENTER);
        return panel;
    }

    // ── Context menu ──────────────────────────────────────────────────────────

    private void showContextMenu(Component c, int x, int y, int row) {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem markPaid = new JMenuItem("✅ Mark as Paid");
        markPaid.addActionListener(e -> updateStatus(selectedBillId, "Paid"));
        menu.add(markPaid);

        JMenuItem markPending = new JMenuItem("⏳ Mark as Pending");
        markPending.addActionListener(e -> updateStatus(selectedBillId, "Pending"));
        menu.add(markPending);

        JMenuItem markPartial = new JMenuItem("💳 Mark as Partially Paid");
        markPartial.addActionListener(e -> updateStatus(selectedBillId, "Partially Paid"));
        menu.add(markPartial);

        menu.addSeparator();

        JMenuItem printItem = new JMenuItem("🖨️ Print Receipt");
        printItem.addActionListener(e -> printReceipt(selectedBillId));
        menu.add(printItem);

        JMenuItem deleteItem = new JMenuItem("🗑️ Delete Bill");
        deleteItem.addActionListener(e -> deleteBill(selectedBillId));
        menu.add(deleteItem);

        menu.show(c, x, y);
    }

    // ── Business logic ────────────────────────────────────────────────────────

    private void findPatient() {
        String phone = patientPhoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a patient phone number.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Patient p = patientDAO.searchPatientByPhone(phone);
        if (p != null) {
            patientNameField.setText(p.getFullName());
            selectedPatientId = p.getPatientId();
        } else {
            JOptionPane.showMessageDialog(this, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
            patientNameField.setText("");
            selectedPatientId = -1;
        }
    }

    private void generateBill() {
        if (selectedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Please find a patient first.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double consultation = parseDouble(consultationField.getText());
            double medicine     = parseDouble(medicineField.getText());
            double room         = parseDouble(roomField.getText());
            double lab          = parseDouble(labField.getText());
            double other        = parseDouble(otherField.getText());
            double discount     = parseDouble(discountField.getText());
            double tax          = parseDouble(taxField.getText());
            double total        = (consultation + medicine + room + lab + other) - discount + tax;

            Bill bill = new Bill(selectedPatientId, 0,
                    consultation, medicine, room, lab, other,
                    discount, tax, total,
                    (String) paymentStatusCombo.getSelectedItem(),
                    (String) paymentModeCombo.getSelectedItem(),
                    notesArea.getText().trim());

            if (billDAO.createBill(bill)) {
                dataChangeManager.notifyBillDataChanged();
                JOptionPane.showMessageDialog(this,
                        "Bill Generated Successfully!\nBill No: " + bill.getBillNumber() +
                        "\nGrand Total: ₹ " + String.format("%.2f", total),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                printReceipt(bill.getBillId());
                clearForm();
                loadBills();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate bill. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "All charge fields must be valid numbers.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateStatus(int billId, String status) {
        if (billId == -1) return;
        if (billDAO.updatePaymentStatus(billId, status)) {
            dataChangeManager.notifyBillDataChanged();
            JOptionPane.showMessageDialog(this, "Status updated to: " + status,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadBills();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBill(int billId) {
        if (billId == -1) return;
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this bill?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (billDAO.deleteBill(billId)) {
                dataChangeManager.notifyBillDataChanged();
                JOptionPane.showMessageDialog(this, "Bill deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                selectedBillId = -1;
                loadBills();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete bill.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printReceipt(int billId) {
        Bill b = billDAO.getBillById(billId);
        if (b == null) {
            JOptionPane.showMessageDialog(this, "Bill not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String receipt =
            "╔══════════════════════════════════════════╗\n" +
            "║       RBH HOSPITAL MANAGEMENT SYSTEM     ║\n" +
            "║              PAYMENT RECEIPT             ║\n" +
            "╠══════════════════════════════════════════╣\n" +
            "  Bill No   : " + b.getBillNumber() + "\n" +
            "  Date      : " + b.getBillDate() + "\n" +
            "  Patient   : " + b.getPatientName() + "\n" +
            "══════════════════════════════════════════\n" +
            "  Consultation Fee : ₹ " + String.format("%10.2f", b.getConsultationFee()) + "\n" +
            "  Medicine         : ₹ " + String.format("%10.2f", b.getMedicineCharges()) + "\n" +
            "  Room Charges     : ₹ " + String.format("%10.2f", b.getRoomCharges()) + "\n" +
            "  Lab Charges      : ₹ " + String.format("%10.2f", b.getLabCharges()) + "\n" +
            "  Other Charges    : ₹ " + String.format("%10.2f", b.getOtherCharges()) + "\n" +
            "──────────────────────────────────────────\n" +
            "  Subtotal         : ₹ " + String.format("%10.2f", b.getSubtotal()) + "\n" +
            "  Discount (-)     : ₹ " + String.format("%10.2f", b.getDiscount()) + "\n" +
            "  Tax / GST (+)    : ₹ " + String.format("%10.2f", b.getTax()) + "\n" +
            "══════════════════════════════════════════\n" +
            "  GRAND TOTAL      : ₹ " + String.format("%10.2f", b.getTotalAmount()) + "\n" +
            "══════════════════════════════════════════\n" +
            "  Payment Mode   : " + b.getPaymentMode() + "\n" +
            "  Payment Status : " + b.getPaymentStatus() + "\n" +
            (b.getNotes() != null && !b.getNotes().isEmpty() ? "  Notes : " + b.getNotes() + "\n" : "") +
            "╚══════════════════════════════════════════╝\n" +
            "         Thank you for choosing RBH!\n";

        JTextArea textArea = new JTextArea(receipt);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setBackground(LIGHT_GRAY);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Print Receipt", true);
        dialog.setSize(500, 520);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton closeBtn = actionButton("CLOSE", DARK_BLUE);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnRow = new JPanel();
        btnRow.setBackground(LIGHT_GRAY);
        btnRow.add(closeBtn);
        dialog.add(btnRow, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void loadBills() {
        tableModel.setRowCount(0);
        ArrayList<Bill> bills = billDAO.getAllBills();
        for (Bill b : bills) {
            tableModel.addRow(new Object[]{
                b.getBillNumber(),
                b.getPatientName(),
                b.getBillDate() != null && b.getBillDate().length() > 10
                        ? b.getBillDate().substring(0, 10) : b.getBillDate(),
                String.format("%.2f", b.getSubtotal()),
                String.format("%.2f", b.getDiscount()),
                String.format("%.2f", b.getTax()),
                String.format("%.2f", b.getTotalAmount()),
                b.getPaymentMode(),
                b.getPaymentStatus()
            });
        }
    }

    private void clearForm() {
        patientPhoneField.setText("");
        patientNameField.setText("");
        consultationField.setText("0");
        medicineField.setText("0");
        roomField.setText("0");
        labField.setText("0");
        otherField.setText("0");
        discountField.setText("0");
        taxField.setText("0");
        notesArea.setText("");
        subtotalLabel.setText("Subtotal: ₹ 0.00");
        grandTotalLabel.setText("Grand Total: ₹ 0.00");
        selectedPatientId = -1;
    }

    private void recalculate() {
        try {
            double sub  = parseDouble(consultationField.getText()) +
                          parseDouble(medicineField.getText()) +
                          parseDouble(roomField.getText()) +
                          parseDouble(labField.getText()) +
                          parseDouble(otherField.getText());
            double disc = parseDouble(discountField.getText());
            double tax  = parseDouble(taxField.getText());
            subtotalLabel.setText("Subtotal: ₹ " + String.format("%.2f", sub));
            grandTotalLabel.setText("Grand Total: ₹ " + String.format("%.2f", sub - disc + tax));
        } catch (NumberFormatException ignored) {}
    }

    private int getBillIdForRow(int row) {
        String billNo = (String) tableModel.getValueAt(row, 0);
        ArrayList<Bill> bills = billDAO.getAllBills();
        for (Bill b : bills) {
            if (b.getBillNumber().equals(billNo)) return b.getBillId();
        }
        return -1;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private void addAutoCalc(JTextField field) {
        field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { recalculate(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { recalculate(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { recalculate(); }
        });
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(DARK_BLUE);
        return l;
    }

    private JLabel miniLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l.setForeground(new Color(80, 80, 80));
        return l;
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        f.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        return f;
    }

    /** Returns a JPanel[label=NORTH, field=CENTER] — caller grabs getComponent(1) for the field. */
    private JPanel labeledField(String label) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(miniLabel(label), BorderLayout.NORTH);
        p.add(styledField(), BorderLayout.CENTER);
        return p;
    }

    private JButton actionButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setBackground(bg);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        return b;
    }

    // ── DataChangeListener ────────────────────────────────────────────────────

    @Override public void onDoctorDataChanged()      { /* no-op */ }
    @Override public void onPatientDataChanged()     { /* no-op */ }
    @Override public void onAppointmentDataChanged() { /* no-op */ }
    @Override public void onBillDataChanged()        { loadBills(); }
}
