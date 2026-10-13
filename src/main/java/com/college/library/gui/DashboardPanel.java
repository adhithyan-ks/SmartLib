package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.Librarian;
import com.college.library.service.AnalyticsService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {
    private MainApplication app;
    private AnalyticsService analyticsService;
    
    private JLabel userGreetingLbl;
    private JLabel totalBooksVal;
    private JLabel availableBooksVal;
    private JLabel issuedBooksVal;
    private JLabel totalStudentsVal;
    private JLabel activeTxVal;

    public DashboardPanel(MainApplication app, AnalyticsService analyticsService) {
        this.app = app;
        this.analyticsService = analyticsService;
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);
        
        // 1. Top Header Navbar
        userGreetingLbl = new JLabel("Welcome back");
        userGreetingLbl.setFont(UITheme.FONT_SUBTITLE);
        userGreetingLbl.setForeground(UITheme.TEXT_SECONDARY);

        JButton logoutBtn = UITheme.createSecondaryButton("Sign Out");
        logoutBtn.addActionListener(e -> {
            app.setLoggedInUser(null);
            app.navigateTo("LOGIN");
        });

        JPanel header = UITheme.createHeaderPanel(
                "Librarian Dashboard",
                "College Library Administration & Circulation",
                null,
                logoutBtn
        );
        add(header, BorderLayout.NORTH);
        
        // 2. Main Content Container
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(UITheme.BG_APP);
        contentPanel.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Quick Navigation Section
        JLabel navHeader = UITheme.createSectionHeader("Quick Navigation");
        contentPanel.add(navHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel navGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        navGrid.setOpaque(false);
        navGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));

        JButton booksBtn = UITheme.createPrimaryButton("Manage Books");
        JButton studentsBtn = UITheme.createPrimaryButton("Manage Students");
        JButton borrowBtn = UITheme.createPrimaryButton("Issue / Return");
        JButton reserveBtn = UITheme.createPrimaryButton("Reservations");

        booksBtn.addActionListener(e -> app.navigateTo("BOOKS"));
        studentsBtn.addActionListener(e -> app.navigateTo("STUDENTS"));
        borrowBtn.addActionListener(e -> app.navigateTo("BORROW"));
        reserveBtn.addActionListener(e -> app.navigateTo("RESERVE"));

        navGrid.add(booksBtn);
        navGrid.add(studentsBtn);
        navGrid.add(borrowBtn);
        navGrid.add(reserveBtn);

        contentPanel.add(navGrid);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 32)));

        // Statistics Section
        JLabel statsHeader = UITheme.createSectionHeader("Library Analytics & Live Counters");
        contentPanel.add(statsHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel statsGrid = new JPanel(new GridLayout(1, 5, 14, 0));
        statsGrid.setOpaque(false);
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        totalBooksVal = new JLabel("-");
        availableBooksVal = new JLabel("-");
        issuedBooksVal = new JLabel("-");
        totalStudentsVal = new JLabel("-");
        activeTxVal = new JLabel("-");

        statsGrid.add(createStatCard("Total Books", totalBooksVal, UITheme.PRIMARY));
        statsGrid.add(createStatCard("Available Books", availableBooksVal, UITheme.SUCCESS));
        statsGrid.add(createStatCard("Issued Books", issuedBooksVal, UITheme.WARNING));
        statsGrid.add(createStatCard("Total Students", totalStudentsVal, new Color(99, 102, 241)));
        statsGrid.add(createStatCard("Active Loans", activeTxVal, UITheme.INFO));

        contentPanel.add(statsGrid);
        contentPanel.add(Box.createVerticalGlue());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        UITheme.CardPanel card = new UITheme.CardPanel(new BorderLayout(0, 8));
        card.setCornerRadius(10);
        card.setBorder(new EmptyBorder(16, 18, 16, 18));

        JLabel titleLbl = new JLabel(title.toUpperCase());
        titleLbl.setFont(UITheme.FONT_STAT_LBL);
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(UITheme.FONT_STAT_NUM);
        valueLabel.setForeground(accentColor);

        // Subtle accent bar on top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(28, 3));
        accentBar.setMaximumSize(new Dimension(28, 3));

        JPanel topBox = new JPanel(new BorderLayout(0, 4));
        topBox.setOpaque(false);
        topBox.add(accentBar, BorderLayout.NORTH);
        topBox.add(titleLbl, BorderLayout.SOUTH);

        card.add(topBox, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
    
    public void refreshData() {
        Librarian current = app.getLoggedInUser();
        if (current != null) {
            userGreetingLbl.setText("Logged in as " + current.getUsername());
        }

        try {
            totalBooksVal.setText(String.valueOf(analyticsService.getTotalBooks()));
            availableBooksVal.setText(String.valueOf(analyticsService.getAvailableBooks()));
            issuedBooksVal.setText(String.valueOf(analyticsService.getIssuedBooks()));
            totalStudentsVal.setText(String.valueOf(analyticsService.getTotalStudents()));
            activeTxVal.setText(String.valueOf(analyticsService.getActiveTransactions()));
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load analytics: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
