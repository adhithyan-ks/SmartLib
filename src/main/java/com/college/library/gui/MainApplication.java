package com.college.library.gui;

import com.college.library.dao.impl.*;
import com.college.library.service.*;
import com.college.library.model.Librarian;

import javax.swing.*;
import java.awt.*;

public class MainApplication extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Services
    private AuthService authService;
    private BookService bookService;
    private StudentService studentService;
    private BorrowService borrowService;
    private ReservationService reservationService;
    private AnalyticsService analyticsService;
    
    private Librarian loggedInUser;
    
    // Panels
    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private BookPanel bookPanel;
    private StudentPanel studentPanel;
    private BorrowPanel borrowPanel;
    private ReservationPanel reservationPanel;

    public MainApplication() {
        initServices();
        initUI();
    }
    
    private void initServices() {
        authService = new AuthService(new LibrarianDAOImpl());
        bookService = new BookService(new BookDAOImpl());
        studentService = new StudentService(new StudentDAOImpl());
        borrowService = new BorrowService(new BorrowTransactionDAOImpl(), new BookDAOImpl(), new StudentDAOImpl(), new ReservationDAOImpl());
        reservationService = new ReservationService(new ReservationDAOImpl(), new StudentDAOImpl(), new BookDAOImpl());
        analyticsService = new AnalyticsService(new BookDAOImpl(), new StudentDAOImpl(), new BorrowTransactionDAOImpl());
    }

    private void initUI() {
        setTitle("SmartLib - College Library Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        loginPanel = new LoginPanel(this, authService);
        dashboardPanel = new DashboardPanel(this, analyticsService);
        bookPanel = new BookPanel(this, bookService);
        studentPanel = new StudentPanel(this, studentService);
        borrowPanel = new BorrowPanel(this, borrowService);
        reservationPanel = new ReservationPanel(this, reservationService, borrowService);
        
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(bookPanel, "BOOKS");
        mainPanel.add(studentPanel, "STUDENTS");
        mainPanel.add(borrowPanel, "BORROW");
        mainPanel.add(reservationPanel, "RESERVE");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }
    
    public void navigateTo(String viewName) {
        cardLayout.show(mainPanel, viewName);
        if ("DASHBOARD".equals(viewName)) dashboardPanel.refreshData();
        else if ("BOOKS".equals(viewName)) bookPanel.refreshData();
        else if ("STUDENTS".equals(viewName)) studentPanel.refreshData();
        else if ("BORROW".equals(viewName)) borrowPanel.refreshData();
        else if ("RESERVE".equals(viewName)) reservationPanel.refreshData();
    }
    
    public void setLoggedInUser(Librarian user) {
        this.loggedInUser = user;
    }
    
    public Librarian getLoggedInUser() {
        return loggedInUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApplication().setVisible(true);
        });
    }
}
