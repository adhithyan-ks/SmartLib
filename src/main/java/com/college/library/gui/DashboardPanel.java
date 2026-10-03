package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.service.AnalyticsService;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private MainApplication app;
    private AnalyticsService analyticsService;
    
    private JLabel totalBooksLbl;
    private JLabel availableBooksLbl;
    private JLabel issuedBooksLbl;
    private JLabel totalStudentsLbl;
    private JLabel activeTxLbl;

    public DashboardPanel(MainApplication app, AnalyticsService analyticsService) {
        this.app = app;
        this.analyticsService = analyticsService;
        setLayout(new BorderLayout());
        
        // Top Navbar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            app.setLoggedInUser(null);
            app.navigateTo("LOGIN");
        });
        navPanel.add(logoutBtn);
        add(navPanel, BorderLayout.NORTH);
        
        // Center Menu & Stats
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton booksBtn = new JButton("Manage Books");
        JButton studentsBtn = new JButton("Manage Students");
        JButton borrowBtn = new JButton("Issue / Return");
        JButton reserveBtn = new JButton("Reservations");
        
        booksBtn.addActionListener(e -> app.navigateTo("BOOKS"));
        studentsBtn.addActionListener(e -> app.navigateTo("STUDENTS"));
        borrowBtn.addActionListener(e -> app.navigateTo("BORROW"));
        reserveBtn.addActionListener(e -> app.navigateTo("RESERVE"));
        
        buttonPanel.add(booksBtn);
        buttonPanel.add(studentsBtn);
        buttonPanel.add(borrowBtn);
        buttonPanel.add(reserveBtn);
        
        // Stats
        JPanel statsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 20, 15));
        statsPanel.setPreferredSize(new Dimension(400, 130));
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Library Analytics"),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        totalBooksLbl = new JLabel("Total Books: ");
        availableBooksLbl = new JLabel("Available Books: ");
        issuedBooksLbl = new JLabel("Issued Books: ");
        totalStudentsLbl = new JLabel("Total Students: ");
        activeTxLbl = new JLabel("Active Transactions: ");
        
        statsPanel.add(totalBooksLbl);
        statsPanel.add(availableBooksLbl);
        statsPanel.add(issuedBooksLbl);
        statsPanel.add(totalStudentsLbl);
        statsPanel.add(activeTxLbl);
        
        statsWrapper.add(statsPanel);
        
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(statsWrapper, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    public void refreshData() {
        try {
            totalBooksLbl.setText("Total Books: " + analyticsService.getTotalBooks());
            availableBooksLbl.setText("Available Books: " + analyticsService.getAvailableBooks());
            issuedBooksLbl.setText("Issued Books: " + analyticsService.getIssuedBooks());
            totalStudentsLbl.setText("Total Students: " + analyticsService.getTotalStudents());
            activeTxLbl.setText("Active Transactions: " + analyticsService.getActiveTransactions());
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load analytics: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
