package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Student;
import com.college.library.service.BorrowService;
import com.college.library.service.ReservationService;
import com.college.library.service.BookService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboardPanel extends JPanel {
    private MainApplication app;
    private BorrowService borrowService;
    private ReservationService reservationService;
    private BookService bookService;

    private JTable searchTable;
    private DefaultTableModel searchTableModel;
    private JTextField searchField;

    private JTable requestsTable;
    private DefaultTableModel requestsTableModel;

    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    
    private JTable reservationTable;
    private DefaultTableModel reservationTableModel;

    public StudentDashboardPanel(MainApplication app, BorrowService borrowService, ReservationService reservationService, BookService bookService) {
        this.app = app;
        this.borrowService = borrowService;
        this.reservationService = reservationService;
        this.bookService = bookService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Navbar
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            app.setLoggedInStudent(null);
            app.navigateTo("LOGIN");
        });
        navPanel.add(logoutBtn);
        add(navPanel, BorderLayout.NORTH);

        // Center Panel for Tables
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));

        // 1. Search Table
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Book Search & Availability"));
        
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        searchBar.add(new JLabel("Search: "));
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchPanel.add(searchBar, BorderLayout.NORTH);

        String[] searchCols = {"ISBN", "Title", "Author", "Category", "Available Copies"};
        searchTableModel = new DefaultTableModel(searchCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        searchTable = new JTable(searchTableModel);
        searchPanel.add(new JScrollPane(searchTable), BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton requestBtn = new JButton("Request to Borrow");
        requestBtn.addActionListener(e -> requestToBorrow());
        actionPanel.add(requestBtn);
        searchPanel.add(actionPanel, BorderLayout.SOUTH);
        
        centerPanel.add(searchPanel);

        // 2. My Borrow Requests Table
        JPanel reqPanel = new JPanel(new BorderLayout());
        reqPanel.setBorder(BorderFactory.createTitledBorder("My Borrow Requests"));
        String[] reqCols = {"Title", "Request Date", "Status"};
        requestsTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestsTable = new JTable(requestsTableModel);
        reqPanel.add(new JScrollPane(requestsTable), BorderLayout.CENTER);
        centerPanel.add(reqPanel);

        // 3. History Table
        String[] historyCols = {"Transaction ID", "Book ID", "Issue Date", "Due Date", "Return Date", "Fine", "Status"};
        historyTableModel = new DefaultTableModel(historyCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("My Borrowing History & Active Loans"));
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        centerPanel.add(historyPanel);

        // 4. Reservation Table
        String[] resCols = {"Reservation ID", "Book ISBN", "Book Title", "Request Date", "Queue Position", "Status"};
        reservationTableModel = new DefaultTableModel(resCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reservationTable = new JTable(reservationTableModel);
        JPanel resPanel = new JPanel(new BorderLayout());
        resPanel.setBorder(BorderFactory.createTitledBorder("My Reservations"));
        resPanel.add(new JScrollPane(reservationTable), BorderLayout.CENTER);
        centerPanel.add(resPanel);

        add(centerPanel, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(searchTable, requestsTable, historyTable, reservationTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{searchTable, requestsTable, historyTable, reservationTable}, requestBtn);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        searchTableModel.setRowCount(0);
        try {
            List<Object[]> results = bookService.searchAvailableBooks(query);
            for (Object[] r : results) {
                searchTableModel.addRow(r);
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to search books: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void requestToBorrow() {
        int selectedRow = searchTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the search results to request.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String isbn = (String) searchTableModel.getValueAt(selectedRow, 0);
        int availableCopies = (Integer) searchTableModel.getValueAt(selectedRow, 4);
        
        if (availableCopies <= 0) {
            JOptionPane.showMessageDialog(this, "This book is currently unavailable. Please use the reservation feature instead.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        try {
            Student student = app.getLoggedInStudent();
            if (student == null) return;
            
            String accessionId = bookService.getFirstAvailableBookId(isbn);
            if (accessionId == null) {
                JOptionPane.showMessageDialog(this, "Failed to find an available copy. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            borrowService.requestToBorrow(student.getKtuId(), accessionId);
            JOptionPane.showMessageDialog(this, "Borrow request submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
            searchTable.clearSelection();
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Request Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshData() {
        Student student = app.getLoggedInStudent();
        if (student == null) return;

        searchField.setText("");
        performSearch();

        requestsTableModel.setRowCount(0);
        historyTableModel.setRowCount(0);
        reservationTableModel.setRowCount(0);

        try {
            List<com.college.library.model.BorrowRequest> requests = borrowService.getStudentBorrowRequests(student.getKtuId());
            for (com.college.library.model.BorrowRequest r : requests) {
                String title = bookService.getBook(r.getBookId()).getTitle();
                requestsTableModel.addRow(new Object[]{
                    title,
                    r.getRequestDate().toString().replace("T", " "),
                    r.getStatus()
                });
            }
            
            List<BorrowTransaction> history = borrowService.getStudentHistory(student.getKtuId());
            for (BorrowTransaction t : history) {
                historyTableModel.addRow(new Object[]{
                        t.getId(),
                        t.getBookId(),
                        t.getIssueDate(),
                        t.getDueDate(),
                        t.getReturnDate(),
                        t.getFine(),
                        t.getStatus()
                });
            }

            List<Object[]> reservations = reservationService.getStudentReservationsWithDetails(student.getKtuId());
            for (Object[] r : reservations) {
                reservationTableModel.addRow(new Object[]{
                        r[0], r[3], r[4], r[5], r[6], r[7]
                });
            }
        } catch (LibraryException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load student data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
