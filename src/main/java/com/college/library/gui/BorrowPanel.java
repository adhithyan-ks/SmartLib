package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowRequest;
import com.college.library.service.BorrowService;

import javax.swing.*;
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

    public BorrowPanel(MainApplication app, BorrowService borrowService) {
        this.app = app;
        this.borrowService = borrowService;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        
        // Top section: Issue and Return
        JPanel formsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        
        // Issue Panel
        JPanel issueWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JPanel issuePanel = new JPanel(new GridLayout(4, 2, 20, 15));
        issuePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Manual Issue"),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        issuePanel.add(new JLabel("Student KTU ID:"));
        issueStudentIdField = new JTextField(15);
        issuePanel.add(issueStudentIdField);
        issuePanel.add(new JLabel("Book Accession ID:"));
        issueBookIdField = new JTextField(15);
        issuePanel.add(issueBookIdField);
        issuePanel.add(new JLabel("")); // spacer
        JButton issueBtn = new JButton("Issue Book");
        issueBtn.addActionListener(e -> attemptIssue());
        issuePanel.add(issueBtn);
        issueWrapper.add(issuePanel);
        
        // Return Panel
        JPanel returnWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JPanel returnPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        returnPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Manual Return"),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        returnPanel.add(new JLabel("Book Accession ID:"));
        returnBookIdField = new JTextField(15);
        returnPanel.add(returnBookIdField);
        returnPanel.add(new JLabel("")); // spacer
        JButton returnBtn = new JButton("Return Book");
        returnBtn.addActionListener(e -> attemptReturn());
        returnPanel.add(returnBtn);
        returnWrapper.add(returnPanel);
        
        formsPanel.add(issueWrapper);
        formsPanel.add(returnWrapper);
        mainContentPanel.add(formsPanel, BorderLayout.NORTH);

        // Bottom section: Pending Requests
        JPanel requestsPanel = new JPanel(new BorderLayout(5, 5));
        requestsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Pending Borrow Requests"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        String[] reqCols = {"Request ID", "Student ID", "Book ID", "Request Date"};
        requestsTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestsTable = new JTable(requestsTableModel);
        requestsPanel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        
        JPanel reqActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton approveBtn = new JButton("Approve Request");
        approveBtn.addActionListener(e -> approveRequest());
        JButton rejectBtn = new JButton("Reject Request");
        rejectBtn.addActionListener(e -> rejectRequest());
        reqActionsPanel.add(approveBtn);
        reqActionsPanel.add(rejectBtn);
        requestsPanel.add(reqActionsPanel, BorderLayout.SOUTH);
        
        mainContentPanel.add(requestsPanel, BorderLayout.CENTER);

        add(mainContentPanel, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(requestsTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{requestsTable}, approveBtn, rejectBtn);
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
            JOptionPane.showMessageDialog(this, "Book successfully issued!");
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
            JOptionPane.showMessageDialog(this, msg);
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
