package com.college.library.gui;

import com.college.library.exception.LibraryException;
import com.college.library.model.BorrowTransaction;
import com.college.library.model.Student;
import com.college.library.service.BorrowService;
import com.college.library.service.ReservationService;
import com.college.library.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private JButton requestBtn;
    private JLabel studentGreetingLbl;

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
        setLayout(new BorderLayout(0, 0));
        setBackground(UITheme.BG_APP);

        // 1. Top Navbar Header
        JButton logoutBtn = UITheme.createSecondaryButton("Sign Out");
        logoutBtn.addActionListener(e -> {
            app.setLoggedInStudent(null);
            app.navigateTo("LOGIN");
        });

        studentGreetingLbl = new JLabel("Student Library Portal");
        studentGreetingLbl.setFont(UITheme.FONT_SUBTITLE);
        studentGreetingLbl.setForeground(UITheme.TEXT_SECONDARY);

        JPanel header = UITheme.createHeaderPanel(
                "Student Portal",
                "Search books, track loans, view requests, and check reservations",
                null,
                logoutBtn
        );
        add(header, BorderLayout.NORTH);

        // 2. Center 2x2 Grid for Content Cards
        JPanel centerGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        centerGrid.setOpaque(false);
        centerGrid.setBorder(new EmptyBorder(16, 20, 16, 20));

        // --- Card 1: Search & Request to Borrow ---
        UITheme.CardPanel searchCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        searchCard.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel searchHeader = new JPanel(new BorderLayout(8, 0));
        searchHeader.setOpaque(false);
        JLabel searchTitle = UITheme.createSectionHeader("Book Search & Availability");
        searchHeader.add(searchTitle, BorderLayout.WEST);

        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchControls.setOpaque(false);
        searchField = UITheme.createTextField(10);
        searchField.setPreferredSize(new Dimension(130, 32));
        searchField.addActionListener(e -> performSearch());

        JButton searchBtn = UITheme.createSecondaryButton("Search");
        searchBtn.addActionListener(e -> performSearch());

        requestBtn = UITheme.createPrimaryButton("Request Borrow");
        requestBtn.addActionListener(e -> requestToBorrow());

        searchControls.add(searchField);
        searchControls.add(searchBtn);
        searchControls.add(requestBtn);
        searchHeader.add(searchControls, BorderLayout.EAST);
        searchCard.add(searchHeader, BorderLayout.NORTH);

        String[] searchCols = {"ISBN", "Title", "Author", "Category", "Available"};
        searchTableModel = new DefaultTableModel(searchCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        searchTable = new JTable(searchTableModel);
        UITheme.styleTable(searchTable);
        searchTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        searchTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        searchTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        searchTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        searchTable.getColumnModel().getColumn(4).setPreferredWidth(70);

        JScrollPane searchScroll = new JScrollPane(searchTable);
        searchScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        searchScroll.getViewport().setBackground(UITheme.BG_CARD);
        searchCard.add(searchScroll, BorderLayout.CENTER);
        centerGrid.add(searchCard);

        // --- Card 2: My Borrow Requests ---
        UITheme.CardPanel reqCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        reqCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel reqTitle = UITheme.createSectionHeader("My Borrow Requests");
        reqCard.add(reqTitle, BorderLayout.NORTH);

        String[] reqCols = {"Title", "Request Date", "Status"};
        requestsTableModel = new DefaultTableModel(reqCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestsTable = new JTable(requestsTableModel);
        UITheme.styleTable(requestsTable);
        requestsTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        requestsTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        requestsTable.getColumnModel().getColumn(2).setPreferredWidth(90);

        JScrollPane reqScroll = new JScrollPane(requestsTable);
        reqScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        reqScroll.getViewport().setBackground(UITheme.BG_CARD);
        reqCard.add(reqScroll, BorderLayout.CENTER);
        centerGrid.add(reqCard);

        // --- Card 3: My History & Active Loans ---
        UITheme.CardPanel historyCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        historyCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel histTitle = UITheme.createSectionHeader("My Borrowing History & Active Loans");
        historyCard.add(histTitle, BorderLayout.NORTH);

        String[] historyCols = {"Tx ID", "Book ID", "Issue Date", "Due Date", "Return Date", "Fine", "Status"};
        historyTableModel = new DefaultTableModel(historyCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        UITheme.styleTable(historyTable);
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(85);
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(85);

        JScrollPane histScroll = new JScrollPane(historyTable);
        histScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        histScroll.getViewport().setBackground(UITheme.BG_CARD);
        historyCard.add(histScroll, BorderLayout.CENTER);
        centerGrid.add(historyCard);

        // --- Card 4: My Reservations ---
        UITheme.CardPanel resCard = new UITheme.CardPanel(new BorderLayout(0, 10));
        resCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel resTitle = UITheme.createSectionHeader("My Book Reservations");
        resCard.add(resTitle, BorderLayout.NORTH);

        String[] resCols = {"Res ID", "ISBN", "Book Title", "Request Date", "Queue", "Status"};
        reservationTableModel = new DefaultTableModel(resCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reservationTable = new JTable(reservationTableModel);
        UITheme.styleTable(reservationTable);
        reservationTable.getColumnModel().getColumn(0).setPreferredWidth(55);
        reservationTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        reservationTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        reservationTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        reservationTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        reservationTable.getColumnModel().getColumn(5).setPreferredWidth(85);

        JScrollPane resScroll = new JScrollPane(reservationTable);
        resScroll.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, UITheme.BORDER));
        resScroll.getViewport().setBackground(UITheme.BG_CARD);
        resCard.add(resScroll, BorderLayout.CENTER);
        centerGrid.add(resCard);

        add(centerGrid, BorderLayout.CENTER);
        
        // Setup custom table selection behavior
        TableSelectionHelper.setupMutuallyExclusiveTables(searchTable, requestsTable, historyTable, reservationTable);
        TableSelectionHelper.setupClickOutsideToClear(this, new JTable[]{searchTable, requestsTable, historyTable, reservationTable}, requestBtn, searchBtn);
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
            if (!student.isActive()) {
                JOptionPane.showMessageDialog(this, "Your account is inactive. You cannot initiate borrowing requests.", "Account Inactive", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
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
                        t.getReturnDate() != null ? t.getReturnDate() : "-",
                        String.format("₹%.2f", t.getFine()),
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
