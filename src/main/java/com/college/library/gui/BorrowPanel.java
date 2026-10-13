package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.service.BorrowService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BorrowPanel extends JPanel {
    private MainApplication app;
    private BorrowService borrowService;
    
    private JTextField issueStudentIdField;
    private JTextField issueBookIdField;
    private JTextField returnBookIdField;

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
                "Issue books, process returns, and approve student requests",
                backBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Main Content Container
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setLayout(new BoxLayout(mainContentPanel, BoxLayout.Y_AXIS));
        mainContentPanel.setOpaque(false);
        mainContentPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        
        // --- Forms Section (Issue & Return Cards) ---
        JPanel formsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        formsPanel.setOpaque(false);
        formsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 165));
        
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
        mainContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // --- Bottom section: Pending Requests Card ---
        UITheme.CardPanel requestsCard = new UITheme.CardPanel(new BorderLayout(0, 12));
        
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

        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        scrollPane.getViewport().setBackground(UITheme.BG_CARD);

        requestsCard.add(scrollPane, BorderLayout.CENTER);
        mainContentPanel.add(requestsCard);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(requestsTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{requestsTable}, approveBtn, rejectBtn, issueBtn, returnBtn);
    }
    
    public void refreshData() {
        issueStudentIdField.setText("");
        issueBookIdField.setText("");
        returnBookIdField.setText("");
        
        loadRequests();
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
