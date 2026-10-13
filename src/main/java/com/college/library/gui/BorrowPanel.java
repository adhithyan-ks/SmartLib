package com.college.library.gui;

import com.college.library.dto.ActiveBorrowingDTO;
import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.service.BorrowService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowPanel extends JPanel {
    private MainApplication app;
    private BorrowService borrowService;
    
    private JTextField issueStudentIdField;
    private JTextField issueBookIdField;
    private JTextField returnBookIdField;

    // Active Borrowings Table & Controls
    private JTable activeTable;
    private DefaultTableModel activeTableModel;
    private JComboBox<String> activeFilterCombo;
    private JTextField activeSearchField;
    private JButton refreshActiveBtn;
    private JButton returnSelectedBtn;
    private List<ActiveBorrowingDTO> rawActiveBorrowings = new ArrayList<>();
    private String activeEmptyMessage = "No active borrowings found.";

    // Pending Borrow Requests Table & Controls
    private JTable requestsTable;
    private DefaultTableModel requestsTableModel;
    private JButton approveBtn;
    private JButton rejectBtn;
    private JButton issueBtn;
    private JButton returnBtn;

    public BorrowPanel(MainApplication app, BorrowService borrowService) {
        this.app = app;
        this.borrowService = borrowService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Header Navbar
        JButton backBtn = UITheme.createBackButton("Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));

        JPanel header = UITheme.createHeaderPanel(
                "Circulation Desk",
                "Issue books, process returns, and manage active borrowings",
                backBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Main Content Container
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // --- Forms Section (Issue & Return Cards) ---
        JPanel formsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        formsPanel.setOpaque(false);
        formsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));
        formsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Manual Issue Card
        UITheme.CardPanel issueCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        JLabel issueTitle = UITheme.createSectionHeader("Direct Book Issue");
        issueCard.add(issueTitle, BorderLayout.NORTH);

        JPanel issueFields = new JPanel(new GridLayout(2, 2, 12, 10));
        issueFields.setOpaque(false);
        issueFields.add(new JLabel("Student KTU ID:"));
        issueStudentIdField = UITheme.createTextField(15);
        issueFields.add(issueStudentIdField);
        issueFields.add(new JLabel("Book Accession ID:"));
        issueBookIdField = UITheme.createTextField(15);
        issueFields.add(issueBookIdField);
        issueCard.add(issueFields, BorderLayout.CENTER);

        JPanel issueActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        issueActionPanel.setOpaque(false);
        issueBtn = UITheme.createPrimaryButton("Issue Book");
        issueBtn.addActionListener(e -> attemptIssue());
        issueActionPanel.add(issueBtn);
        issueCard.add(issueActionPanel, BorderLayout.SOUTH);

        // Manual Return Card
        UITheme.CardPanel returnCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        JLabel returnTitle = UITheme.createSectionHeader("Direct Book Return");
        returnCard.add(returnTitle, BorderLayout.NORTH);

        JPanel returnFields = new JPanel(new GridLayout(2, 2, 12, 10));
        returnFields.setOpaque(false);
        returnFields.add(new JLabel("Book Accession ID:"));
        returnBookIdField = UITheme.createTextField(15);
        returnFields.add(returnBookIdField);
        returnFields.add(new JLabel("")); // spacer
        returnFields.add(new JLabel("")); // spacer
        returnCard.add(returnFields, BorderLayout.CENTER);

        JPanel returnActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        returnActionPanel.setOpaque(false);
        returnBtn = UITheme.createSuccessButton("Return Book");
        returnBtn.addActionListener(e -> attemptReturn());
        returnActionPanel.add(returnBtn);
        returnCard.add(returnActionPanel, BorderLayout.SOUTH);

        formsPanel.add(issueCard);
        formsPanel.add(returnCard);
        mainContentPanel.add(formsPanel);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // --- Middle Section: Active Borrowings Card ---
        UITheme.CardPanel activeBorrowingsCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        activeBorrowingsCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel activeHeaderPanel = new JPanel(new BorderLayout(10, 0));
        activeHeaderPanel.setOpaque(false);

        JLabel activeTitle = UITheme.createSectionHeader("Active Borrowings");
        activeHeaderPanel.add(activeTitle, BorderLayout.WEST);

        JPanel activeControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        activeControls.setOpaque(false);

        JLabel filterLbl = new JLabel("Filter:");
        filterLbl.setFont(UITheme.FONT_BODY_BOLD);
        filterLbl.setForeground(UITheme.TEXT_SECONDARY);
        activeControls.add(filterLbl);

        activeFilterCombo = new JComboBox<>(new String[]{"All Active", "Overdue", "Due Today"});
        UITheme.styleComboBox(activeFilterCombo);
        activeFilterCombo.setPreferredSize(new Dimension(115, 30));
        activeFilterCombo.addActionListener(e -> applyActiveBorrowingsFilter());
        activeControls.add(activeFilterCombo);

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(UITheme.FONT_BODY_BOLD);
        searchLbl.setForeground(UITheme.TEXT_SECONDARY);
        activeControls.add(searchLbl);

        activeSearchField = UITheme.createTextField(10);
        activeSearchField.setPreferredSize(new Dimension(130, 30));
        activeSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyActiveBorrowingsFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyActiveBorrowingsFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyActiveBorrowingsFilter(); }
        });
        activeControls.add(activeSearchField);

        refreshActiveBtn = UITheme.createSecondaryButton("Refresh");
        refreshActiveBtn.addActionListener(e -> loadActiveBorrowings());
        activeControls.add(refreshActiveBtn);

        returnSelectedBtn = UITheme.createSuccessButton("Return Selected Book");
        returnSelectedBtn.addActionListener(e -> attemptReturnFromTable());
        activeControls.add(returnSelectedBtn);

        activeHeaderPanel.add(activeControls, BorderLayout.EAST);
        activeBorrowingsCard.add(activeHeaderPanel, BorderLayout.NORTH);

        String[] activeCols = {
                "Transaction ID", "Student ID", "Student Name",
                "Book Accession ID", "Book Title", "Issue Date",
                "Due Date", "Days Overdue", "Current Fine", "Status"
        };
        activeTableModel = new DefaultTableModel(activeCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        activeTable = new JTable(activeTableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                    g2.setFont(UITheme.FONT_BODY);
                    g2.setColor(UITheme.TEXT_MUTED);
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(activeEmptyMessage)) / 2;
                    int y = Math.max(fm.getHeight() + 30, getHeight() / 2);
                    g2.drawString(activeEmptyMessage, Math.max(10, x), y);
                    g2.dispose();
                }
            }
        };
        UITheme.styleTable(activeTable);

        activeTable.getColumnModel().getColumn(0).setPreferredWidth(95);  // Transaction ID
        activeTable.getColumnModel().getColumn(1).setPreferredWidth(105); // Student ID
        activeTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Student Name
        activeTable.getColumnModel().getColumn(3).setPreferredWidth(115); // Book Accession ID
        activeTable.getColumnModel().getColumn(4).setPreferredWidth(180); // Book Title
        activeTable.getColumnModel().getColumn(5).setPreferredWidth(95);  // Issue Date
        activeTable.getColumnModel().getColumn(6).setPreferredWidth(95);  // Due Date
        activeTable.getColumnModel().getColumn(7).setPreferredWidth(90);  // Days Overdue
        activeTable.getColumnModel().getColumn(8).setPreferredWidth(95);  // Current Fine
        activeTable.getColumnModel().getColumn(9).setPreferredWidth(90);  // Status

        activeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = activeTable.getSelectedRow();
                if (selectedRow != -1) {
                    int modelRow = activeTable.convertRowIndexToModel(selectedRow);
                    Object bookIdVal = activeTableModel.getValueAt(modelRow, 3);
                    if (bookIdVal != null) {
                        returnBookIdField.setText(bookIdVal.toString());
                    }
                }
            }
        });

        JScrollPane activeScrollPane = new JScrollPane(activeTable);
        activeScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        activeScrollPane.getViewport().setBackground(UITheme.BG_CARD);
        activeScrollPane.setPreferredSize(new Dimension(activeScrollPane.getPreferredSize().width, 210));

        activeBorrowingsCard.add(activeScrollPane, BorderLayout.CENTER);
        mainContentPanel.add(activeBorrowingsCard);
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // --- Bottom Section: Pending Requests Card ---
        UITheme.CardPanel requestsCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        requestsCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel reqHeaderPanel = new JPanel(new BorderLayout());
        reqHeaderPanel.setOpaque(false);
        JLabel reqTitle = UITheme.createSectionHeader("Pending Student Borrow Requests");
        reqHeaderPanel.add(reqTitle, BorderLayout.WEST);

        JPanel reqActionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        reqActionsPanel.setOpaque(false);
        approveBtn = UITheme.createSuccessButton("Approve Request");
        approveBtn.addActionListener(e -> approveRequest());
        rejectBtn = UITheme.createDangerButton("Reject Request");
        rejectBtn.addActionListener(e -> rejectRequest());
        reqActionsPanel.add(approveBtn);
        reqActionsPanel.add(rejectBtn);
        reqHeaderPanel.add(reqActionsPanel, BorderLayout.EAST);

        requestsCard.add(reqHeaderPanel, BorderLayout.NORTH);

        String[] reqCols = {"Request ID", "Student ID", "Book ID", "Request Date"};
        requestsTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestsTable = new JTable(requestsTableModel);
        UITheme.styleTable(requestsTable);

        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        requestsTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        requestsTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        requestsTable.getColumnModel().getColumn(3).setPreferredWidth(220);

        JScrollPane reqScrollPane = new JScrollPane(requestsTable);
        reqScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        reqScrollPane.getViewport().setBackground(UITheme.BG_CARD);
        reqScrollPane.setPreferredSize(new Dimension(reqScrollPane.getPreferredSize().width, 160));

        requestsCard.add(reqScrollPane, BorderLayout.CENTER);
        mainContentPanel.add(requestsCard);

        JScrollPane outerScrollPane = new JScrollPane(mainContentPanel);
        outerScrollPane.setBorder(null);
        outerScrollPane.setOpaque(false);
        outerScrollPane.getViewport().setOpaque(false);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(outerScrollPane, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(activeTable, requestsTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{activeTable, requestsTable},
                approveBtn, rejectBtn, issueBtn, returnBtn, returnSelectedBtn, refreshActiveBtn, activeFilterCombo);
    }
    
    public void refreshData() {
        issueStudentIdField.setText("");
        issueBookIdField.setText("");
        returnBookIdField.setText("");
        
        loadActiveBorrowings();
        loadRequests();
    }

    private void loadActiveBorrowings() {
        try {
            rawActiveBorrowings = borrowService.getAllActiveBorrowings();
            applyActiveBorrowingsFilter();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load active borrowings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyActiveBorrowingsFilter() {
        activeTableModel.setRowCount(0);
        String filter = activeFilterCombo != null ? (String) activeFilterCombo.getSelectedItem() : "All Active";
        String search = activeSearchField != null ? activeSearchField.getText() : "";
        List<ActiveBorrowingDTO> filtered = borrowService.filterActiveBorrowings(rawActiveBorrowings, filter, search);

        if (rawActiveBorrowings.isEmpty()) {
            activeEmptyMessage = "No active borrowings found.";
        } else if (filtered.isEmpty()) {
            activeEmptyMessage = "No active borrowings match the selected filter.";
        }

        for (ActiveBorrowingDTO b : filtered) {
            activeTableModel.addRow(new Object[]{
                    b.getTransactionId(),
                    b.getStudentId(),
                    b.getStudentName() != null ? b.getStudentName() : "-",
                    b.getBookAccessionId(),
                    b.getBookTitle() != null ? b.getBookTitle() : "-",
                    b.getIssueDate() != null ? b.getIssueDate().toString() : "-",
                    b.getDueDate() != null ? b.getDueDate().toString() : "-",
                    b.getDaysOverdue(),
                    String.format("₹%.2f", b.getCurrentFine()),
                    b.getStatus()
            });
        }
        activeTable.repaint();
    }
    
    private void loadRequests() {
        requestsTableModel.setRowCount(0);
        try {
            List<BorrowRequest> pending = borrowService.getAllPendingBorrowRequests();
            for (BorrowRequest req : pending) {
                requestsTableModel.addRow(new Object[]{
                    req.getId(),
                    req.getStudentId(),
                    req.getBookId(),
                    req.getRequestDate().toString().replace("T", " ")
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load requests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptIssue() {
        try {
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            borrowService.issueBook(issueStudentIdField.getText().trim(), issueBookIdField.getText().trim(), librarianId);
            JOptionPane.showMessageDialog(this, "Book successfully issued!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptReturn() {
        try {
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            String msg = borrowService.returnBook(returnBookIdField.getText().trim(), librarianId);
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptReturnFromTable() {
        int selectedRow = activeTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an active borrowing to return.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = activeTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= activeTableModel.getRowCount()) {
            return;
        }
        Object bookIdObj = activeTableModel.getValueAt(modelRow, 3);
        String bookId = bookIdObj != null ? bookIdObj.toString().trim() : "";
        if (bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selected row has no valid book accession ID.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        returnSelectedBtn.setEnabled(false);
        try {
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            String msg = borrowService.returnBook(bookId, librarianId);
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            activeTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            refreshData();
        } finally {
            returnSelectedBtn.setEnabled(true);
        }
    }
    
    private void approveRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int requestId = (Integer) requestsTableModel.getValueAt(selectedRow, 0);
        try {
            if (app.getLoggedInUser() == null) throw new LibraryException("Librarian not logged in!");
            int librarianId = app.getLoggedInUser().getId();
            String msg = borrowService.approveBorrowRequest(requestId, librarianId);
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            requestsTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void rejectRequest() {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to reject.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int requestId = (Integer) requestsTableModel.getValueAt(selectedRow, 0);
        try {
            borrowService.rejectBorrowRequest(requestId);
            JOptionPane.showMessageDialog(this, "Request rejected.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            requestsTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
