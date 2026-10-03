package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.service.BorrowService;

import javax.swing.*;
import java.awt.*;

public class BorrowPanel extends JPanel {
    private MainApplication app;
    private BorrowService borrowService;
    
    private JTextField issueStudentIdField;
    private JTextField issueBookIdField;
    private JTextField returnBookIdField;

    public BorrowPanel(MainApplication app, BorrowService borrowService) {
        this.app = app;
        this.borrowService = borrowService;
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("Back to Dashboard");
        backBtn.addActionListener(e -> app.navigateTo("DASHBOARD"));
        topPanel.add(backBtn);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        
        // Issue Panel
        JPanel issuePanel = new JPanel(new GridLayout(4, 2, 10, 10));
        issuePanel.setBorder(BorderFactory.createTitledBorder("Issue Book"));
        issuePanel.add(new JLabel("Student KTU ID:"));
        issueStudentIdField = new JTextField();
        issuePanel.add(issueStudentIdField);
        issuePanel.add(new JLabel("Book Accession ID:"));
        issueBookIdField = new JTextField();
        issuePanel.add(issueBookIdField);
        issuePanel.add(new JLabel("")); // spacer
        JButton issueBtn = new JButton("Issue Book");
        issueBtn.addActionListener(e -> attemptIssue());
        issuePanel.add(issueBtn);
        
        // Return Panel
        JPanel returnPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        returnPanel.setBorder(BorderFactory.createTitledBorder("Return Book"));
        returnPanel.add(new JLabel("Book Accession ID:"));
        returnBookIdField = new JTextField();
        returnPanel.add(returnBookIdField);
        returnPanel.add(new JLabel("")); // spacer
        JButton returnBtn = new JButton("Return Book");
        returnBtn.addActionListener(e -> attemptReturn());
        returnPanel.add(returnBtn);
        
        centerPanel.add(issuePanel);
        centerPanel.add(returnPanel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        issueStudentIdField.setText("");
        issueBookIdField.setText("");
        returnBookIdField.setText("");
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
}
